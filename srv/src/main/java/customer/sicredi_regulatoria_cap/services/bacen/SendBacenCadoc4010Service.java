package customer.sicredi_regulatoria_cap.services.bacen;

import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.http.HttpException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.balanceservice.Balance;
import cds.gen.sicredi.db.entities.Companies;
import cds.gen.sicredi.db.entities.Companies_;
import cds.gen.sicredi.db.entities.Protocols;
import cds.gen.sicredi.db.entities.Protocols_;
import cds.gen.sicredi.db.entities.Status;
import customer.sicredi_regulatoria_cap.dtos.bacen.ResponseSendBacenDTO;
import customer.sicredi_regulatoria_cap.dtos.bacen.SendBacenDTO;
import customer.sicredi_regulatoria_cap.dtos.bacen.SendBacenDTO.AccountDTO;
import customer.sicredi_regulatoria_cap.dtos.balance.BalanceFilterDTO;
import customer.sicredi_regulatoria_cap.services.APIDestinationService;
import customer.sicredi_regulatoria_cap.services.CompanyService;
import customer.sicredi_regulatoria_cap.services.PropertiesService;
import customer.sicredi_regulatoria_cap.services.ReportService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SendBacenCadoc4010Service {

	@Autowired
	private PersistenceService db;

	@Autowired
	private ReadBacenService readBacenService;

	@Autowired
	private PropertiesService propertiesService;

	@Autowired
	private ReportService reportService;

	@Autowired
	private APIDestinationService apiDestinationService;

	@Autowired
	private CompanyService companyService;

	@Autowired
	private ObjectMapper mapper;

	public void sendBacen(BalanceFilterDTO filterDTO, Boolean updateDocumentsBacen, String user) throws UnsupportedCharsetException, IOException, HttpException {
		createBacen(filterDTO, updateDocumentsBacen, user);
	}

	public boolean areDocumentsSent(BalanceFilterDTO filterDTO) {
		return db.run(
				Select.from(Protocols_.class)
						.where(
								p -> p.company_externalCode().in(filterDTO.getCompanies()).and(
										p.exercise().eq(filterDTO.getExercise()).and(
												p.version().eq(filterDTO.getVersion()).and(
														p.interval().eq(filterDTO.getInterval()).and(
																p.tipoDoc().in(filterDTO.getTipoDoc()).and(
																		p.releaseDate().eq(filterDTO.getReleaseDate())
																				.and(
																						p.bloc().eq(filterDTO.getBloc()
																								.getText()).and(
																										p.cadoc().eq(
																												filterDTO
																														.getCadoc()
																														.getName()//
																										)//
																						)//
																				)//
																)//
														)//
												)//
										)//
								)//
						)//
		).rowCount() > 0;
	}

	public void createBacen(BalanceFilterDTO filterDTO, Boolean updateDocumentsBacen, String user)
			throws UnsupportedCharsetException, IOException, HttpException {

		mapper.findAndRegisterModules();
		mapper.disable(DeserializationFeature.UNWRAP_ROOT_VALUE);
		mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);

		log.info("{} companies in filter", String.join(",", filterDTO.getCompanies()));

		List<Companies> companies = db.run(
				Select.from(Companies_.class)//
						.columns(//
								c -> c.cnpj(), //
								c -> c.externalCode())//
						.where(//
								c -> c.externalCode().in(filterDTO.getCompanies())//
						)//
		).listOf(Companies.class);


		Integer exercise = Integer.parseInt(filterDTO.getExercise());
		Integer interval = Integer.parseInt(filterDTO.getInterval());
		interval = interval > 12 ? 12 : interval;

		LocalDate baseDate = LocalDate.of(exercise, interval, 1);

		log.info("{} companies to send", companies.size());
		for (Companies company : companies) {
			if (Objects.isNull(company.getCnpj()) || company.getCnpj().length() < 8) {
				continue;
			}
			
			BalanceFilterDTO filterCompanyDTO = filterDTO.toBuilder().companies(Arrays.asList(company.getExternalCode())).build();

			List<Balance> balances = readBacenService.getBalances(
				filterCompanyDTO,
				e -> e.razaoAccount().eq("").or(e.razaoAccount().isNull())
			);

			if (balances.isEmpty()) {
				log.info("Company {} accounts is empty", company.getExternalCode());
				continue;
			}

			
			Protocols protocol = reportService.createReport(filterCompanyDTO, company, user);
			log.info("Protocol {} for company {}", protocol.getId(), company.getExternalCode());
			
			Stream<Balance> balancesStream = balances.stream();
			
			List<AccountDTO> accounts = reportService.addDetails(protocol, balancesStream, filterCompanyDTO);
			
			var auth = companyService.getCompanyAuthenticator(company);

			String authBase64 = Base64.getEncoder().encodeToString(
				(auth.get().getUser() + ":" + auth.get().getPassword()).getBytes()
			);

			SendBacenDTO bacenDTO = SendBacenDTO.builder()//
					.cnpj(company.getCnpj().substring(0, 8))//
					.cadoc(filterCompanyDTO.getCadoc().getName())//
					.accounts(accounts)//
					.baseDate(baseDate)//
					.remittanceType(filterDTO.getRemittanceType())//
					.build();

			final StringEntity entity = new StringEntity(//
					mapper.writeValueAsString(bacenDTO),
					ContentType.APPLICATION_JSON//
			);

			Optional<ResponseSendBacenDTO> optResponse = Optional.empty();
			try {

				optResponse = apiDestinationService.post(//
					"/http/Cap_In_Bacen_DataTransfer", //
					propertiesService.getCloudDestinationApi(), //
					entity, ResponseSendBacenDTO.class, //
					code -> code == 200, //
					mapper,
					new BasicHeader("Company-Authorization", authBase64)
				);
				
			} catch (Exception e) {
				log.error("Company {}, error request {}", company.getExternalCode(), e.getMessage());
				updateErrorProtocol(protocol, "Error Requisição");
				continue;
			}
				
			if (optResponse.isEmpty() || Objects.isNull(optResponse.get())) {
				updateErrorProtocol(protocol, "Protocolo Indisponivel");
				log.error("Company {}, Response Send to Bacen is Empty", company.getExternalCode());
				continue;
				// throw new ServiceException(ErrorStatuses.SERVER_ERROR, "Response Send to Bacen is empty");
			}

			protocol.setProtocol(optResponse.get().getProtocol());
			protocol.setStatus(Status.GENERATED);

			db.run(//
					Update.entity(Protocols_.class)//
							.data(protocol)//
							.byId(protocol.getId())//
			);
			log.info("Protocol {} for company {} was sent", protocol.getId(), company.getExternalCode());
			
		}
	}

	private void updateErrorProtocol(Protocols protocol, String protocolValue) {
		protocol.setStatus(Status.FAILURE);
		protocol.setProtocol(protocolValue);

		db.run(//
			Update.entity(Protocols_.class)//
				.data(protocol)//
				.byId(protocol.getId())//
		);
	}
}
