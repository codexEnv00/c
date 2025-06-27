package customer.sicredi_regulatoria_cap.services;

import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.function.TriFunction;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import com.sap.cds.Result;
import com.sap.cds.ql.cqn.CqnPredicate;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;

import cds.gen.sicredi.db.entities.Balances_;
import customer.sicredi_regulatoria_cap.dtos.balance.BalanceFilterDTO;
import customer.sicredi_regulatoria_cap.repositories.OutsideBalanceRepository;
import customer.sicredi_regulatoria_cap.repositories.TerminationDocumentRepository;
import customer.sicredi_regulatoria_cap.services.bacen.ReadBacenService;
import customer.sicredi_regulatoria_cap.services.bacen.SendBacenCadoc4010Service;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BacenService {

	@Autowired
	private ReadBacenService readBacenService;

	@Autowired
	private SendBacenCadoc4010Service sendBacenService;

	@Autowired
	private TerminationDocumentRepository terminationDocumentRepository;

	@Autowired
	private OutsideBalanceRepository outsideBalanceRepository;

	private final ConcurrentHashMap<String, AsyncLoadBalanceResult> processLoadBalanceAsync = new ConcurrentHashMap<>();

	public AsyncLoadBalanceResult getStatus(String id) {
		return Optional.ofNullable(processLoadBalanceAsync.get(id)).orElseThrow(() -> new ServiceException(ErrorStatuses.BAD_REQUEST, "ID nao disponivel"));
	}

	@Scheduled(fixedDelayString = "${bacen.clean.load.balance.delay.time:300000}")
	public void cleanOldLoadBalance() {
		log.info("Processo de limpeza de processamento async");
		for (var entry : processLoadBalanceAsync.entrySet()) {
			if (entry.getValue().finishDateTime == null) {
				continue;
			}


			if (Duration.between(entry.getValue().finishDateTime, LocalDateTime.now()).toMinutes() < 5) {
				continue;
			}

			log.info("Limpando ID {}", entry.getKey());
			processLoadBalanceAsync.remove(entry.getKey());
		}
	}

	public String generateLoadBalanceAsyncId(BalanceFilterDTO filterDTO) throws IOException, HttpException {
		var id = UUID.randomUUID();
		processLoadBalanceAsync.put(id.toString(), new AsyncLoadBalanceResult("PROCESSING", "", null));
		readBacenService.loadBacenAsync(filterDTO, id.toString())
		.thenAccept(e -> {
			processLoadBalanceAsync.put(e, new AsyncLoadBalanceResult("COMPLETE", "Finalizado com sucesso", LocalDateTime.now()));
		})
		.exceptionally(erro -> {
			processLoadBalanceAsync.put(id.toString(), new AsyncLoadBalanceResult("ERROR", erro.getMessage(), LocalDateTime.now()));
			return null;
		});

		
		return id.toString();
	}

	public Result getBacen(BalanceFilterDTO filterDTO, CqnSelect select, String nodeId) {
		// loadBalance(filterDTO);
		Function<Balances_, CqnPredicate> where;
		if (Objects.isNull(nodeId)) {
			//where = e -> e.level().eq(1);
			where = null;
		}
		else if (nodeId.equals("1")) {
			where = e -> e.level().eq(1);
		}
		else {
			where = e -> e.parentKey().eq(nodeId);
		}
		return readBacenService.getBalances(filterDTO, select.skip(), select.top(), where);
	}

	public Result getTerminationDocument(BalanceFilterDTO filterDTO, CqnSelect select) {
		return terminationDocumentRepository.findAllByFilterWithCount(filterDTO, select.top(), select.skip());
	}

	public Result getOutsideBalances(BalanceFilterDTO filterDTO, CqnSelect select) {
		return outsideBalanceRepository.findAllByFilterWithCount(filterDTO, select.top(), select.skip());
	}

	public void loadBalance(BalanceFilterDTO filterDTO) {
		try {
			readBacenService.loadBacen(filterDTO);
		} catch (IOException | HttpException e) {
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, e.getMessage());
		}
	}

	public SendBacenResponse transmitToBacen(BalanceFilterDTO filterDTO, Boolean updateDocumentsBacen, String user) {
		try {
			sendBacenService.sendBacen(filterDTO, updateDocumentsBacen, user);
			return new SendBacenResponse("Documents sent", true, false);
		} catch (UnsupportedCharsetException | IOException | HttpException e) {
			throw new ServiceException(ErrorStatuses.SERVER_ERROR, e.getMessage());
		}
	}

	public record SendBacenResponse(String message, boolean sent, boolean userConfirmation) {
	}

	@AllArgsConstructor
	@Getter
	public enum ECadoc {
		C4010("4010", "/http/cadoc4010/getCadocSet", 
			(filterDTO, isTab2, builder) -> {
				return builder
					.queryParam("$expand", "to_tab1,to_tab2,to_tab3")
					.queryParam("$filter",
							filterDTO.toQuery() + " and " + (isTab2 ? "Tab2 eq 'X'" : "Tab3 eq 'X'"));
			}
		),
		C4016("4016", "/http/cadoc4016/getCadocSet", C4010.getFunctionPathParameters()),
		C4500("4500", "http/cadoc4500/getCadocSet", 
			(filterDTO, isTab2, builder) -> {
				return builder.queryParam(//
						"$filter",
						new StringBuilder(
							MessageFormat.format("Periodo eq ''{0}'' and Exercicio eq ''{1}'' and ", filterDTO.getInterval(), filterDTO.getExercise())
						)
						.append("(")
						.append(
							filterDTO.getCompanies().stream()//
								.map(e -> "Empresa eq '" + e + "'")
								.collect(Collectors.joining(" or "))
						)
						.append(")")
						.toString()
					);
			}
		);

		private final String name;
		
		@Getter(AccessLevel.NONE)
		private final String path;
		
		private final TriFunction<BalanceFilterDTO, Boolean, UriComponentsBuilder, UriComponentsBuilder> functionPathParameters;

		public static Optional<ECadoc> getCadoc(String cadoc) {
			return Stream.of(ECadoc.values()).filter(e -> e.name.equals(cadoc)).findFirst();
		}

		public String getPath(BalanceFilterDTO filterDTO, boolean isTab2) {

			UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
					.path(path)
					.queryParam("$format", "json");

			builder = functionPathParameters.apply(filterDTO, isTab2, builder);
			
			return builder.build()
					.encode()
					.toUriString();
		}

		public String toString() {
			return name;
		}
	}

	@AllArgsConstructor
	@Getter
	public enum BlocoMeses {
		JUN("BlcJun"),
		DEZ("BlcDez");

		private String text;

		public static Optional<BlocoMeses> getFromText(String text) {
			if ("jun".equalsIgnoreCase(text)) {
				return Optional.of(JUN);
			}
			if ("dez".equalsIgnoreCase(text)) {
				return Optional.of(DEZ);
			}

			return Optional.empty();
		}
	}
	
	public record AsyncLoadBalanceResult (String status, String message, LocalDateTime finishDateTime) {};
}
