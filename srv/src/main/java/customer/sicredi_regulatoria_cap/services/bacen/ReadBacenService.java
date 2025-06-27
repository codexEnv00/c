package customer.sicredi_regulatoria_cap.services.bacen;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sap.cds.Result;
import com.sap.cds.ResultBuilder;
import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Predicate;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Upsert;
import com.sap.cds.ql.cqn.CqnPredicate;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.balanceservice.Balance;
import cds.gen.sicredi.db.entities.Balances;
import cds.gen.sicredi.db.entities.Balances_;
import cds.gen.sicredi.db.entities.OutsideBalance;
import cds.gen.sicredi.db.entities.OutsideBalance_;
import cds.gen.sicredi.db.entities.TerminationDocument;
import cds.gen.sicredi.db.entities.TerminationDocument_;
import customer.sicredi_regulatoria_cap.dtos.WrapperResultDTO;
import customer.sicredi_regulatoria_cap.dtos.bacen.BacenDTO;
import customer.sicredi_regulatoria_cap.dtos.balance.BalanceFilterDTO;
import customer.sicredi_regulatoria_cap.dtos.balance.BalanceResponseDTO;
import customer.sicredi_regulatoria_cap.mappers.BacenMapper;
import customer.sicredi_regulatoria_cap.services.APIDestinationService;
import customer.sicredi_regulatoria_cap.services.CompanyService;
import customer.sicredi_regulatoria_cap.services.PropertiesService;
import customer.sicredi_regulatoria_cap.utils.CompaniesWrapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReadBacenService {

	@Autowired
	private APIDestinationService apiDestinationService;

	@Autowired
	private PropertiesService propertiesService;

	@Autowired
	private CompanyService companyService;

	@Autowired
	private PersistenceService db;


	@Async
	public CompletableFuture<String> loadBacenAsync(BalanceFilterDTO filterDTO, String id) throws IOException, HttpException {
		loadBacen(filterDTO);

		return CompletableFuture.completedFuture(id);
	}

	@Cacheable(value = "balances", keyGenerator = "balanceKeyGenerator")
	public boolean loadBacen(BalanceFilterDTO filterDTO) throws IOException, HttpException {
		final Set<CompaniesWrapper> mapCompany = ConcurrentHashMap.newKeySet();

		companyService.loadCompanies();


		List<String> companies = filterDTO.getCompanies();

		for (var e : ListUtils.partition(companies, 5)) {
			log.info("Getting Documents and Outside Documents for company {}", e);
			int count = 0;

			do {
				try {
					loadTab1Bacen(filterDTO.toBuilder().companies(e).build(), mapCompany);
				} catch (IOException | HttpException e1) {
					log.error(e1.getMessage());
					if (e1.getMessage().contains("processar essa quantidade de empresa no momento.")) {
						try {
							Thread.sleep(Duration.of(5, ChronoUnit.SECONDS));
						} catch (InterruptedException e2) {}
						count++;
						
						if (count >= 10) {
							throw new ServiceException(ErrorStatuses.SERVER_ERROR, "Processamento ECC sobrecarregado");
						}
						continue;
					}

					count++;
					if (count >= 10) {
						throw e1;
					}
				}

				break;
			} while (count < 10);
		};

		ListUtils.partition(companies, 3).stream().forEach(e -> {
			
			log.info("Getting termination documents from CI for company {}", e);
			try {
				loadTab2Bacen(filterDTO.toBuilder().companies(e).build(), mapCompany);
			} catch (IOException | HttpException e1) {
				log.error(e1.getMessage());
			}
		});

		return true;
	}

	public Stream<BalanceResponseDTO> getBalanceStream(BalanceFilterDTO filterDTO) {
		return getBalanceStream(filterDTO, null);
	}

	public Stream<BalanceResponseDTO> getBalanceStream(BalanceFilterDTO filterDTO, Function<Balances_, CqnPredicate> additionalWhere) {
		Function<Balances_, CqnPredicate> where = e -> {
			Predicate predicate = e.cadoc().eq(filterDTO.getCadoc().getName())
			.and(//
					e.company_externalCode().in(filterDTO.getCompanies()).and(//
							e.exercise().eq(filterDTO.getExercise()).and(//
									e.version().eq(filterDTO.getVersion()).and(//
											e.interval().eq(filterDTO.getInterval())//
									)//
							)//
					)//
			);
			if (Objects.nonNull(additionalWhere)) {
				predicate = predicate.and(additionalWhere.apply(e));
			}
			return predicate;
		};
		return db.run(
				Select.from(Balances_.class)//
						.columns(
								b -> b.nodeID(),
								b -> b.level().minus(1).as("hierarchyLevel"),
								b -> b.parentKey().as("parentNodeID"),
								b -> b.drillState(),
								b -> b.company_externalCode().as("company"),
								b -> b.company().cnpj(),
								b -> b.cosifAccount().as("cosif"),
								b -> b.razaoAccount().as("razao"),
								b -> b.description(),
								b -> b.cadocBalance(),
								b -> b.cadocBalance().as("originalCadocBalance"))
						.where(where)//
						.orderBy(e -> e.company_externalCode().asc(), e -> e.order().asc())//
		).streamOf(BalanceResponseDTO.class);
	}

	public Map<CompanyAccount, BigDecimal> getTerminationDocuments(BalanceFilterDTO filterDTO) {
		filterDTO.generateInterval();
		filterDTO.generateTpDoc();
		filterDTO.generateReleaseDate();
		return db.run(//
				Select.from(TerminationDocument_.class)//
						.columns(//
								t -> t.company().expand(//
										c -> c.externalCode() //
								), //
								t -> t.razao(),
								t -> t.amount().sum().as("amount")//
						)
						.where(
								t -> t.company().externalCode().in(
										Optional.ofNullable(filterDTO.getTerminationCompanies()).orElse(new ArrayList<>())
												.stream()
												.toList())
										.and(
												t.exercise().eq(filterDTO.getExercise()).and(
														t.interval().eq(filterDTO.getInterval()).and(
																t.cadoc().eq(filterDTO.getCadoc().getName()).and(
																		t.dtLanc().eq(filterDTO.getReleaseDate()).and(
																				t.tpDoc().in(
																						filterDTO.getTipoDoc())))))))
						.groupBy(
								t -> t.company().externalCode(),
								t -> t.razao()))
				.streamOf(TerminationDocument.class)//
				.parallel()//
				.collect(Collectors.toMap(t -> new CompanyAccount(t.getCompany().getExternalCode(), t.getRazao()),
						TerminationDocument::getAmount));
	}

	public void processBalanceTermination(Map<CompanyAccount, BigDecimal> mapTerminationDocuments,
			BalanceResponseDTO current, AtomicReference<BalanceResponseDTO> previousRef) {
		BalanceResponseDTO previous = previousRef.get();

		if (previous != null) {
			if (current.getHierarchyLevel() > previous.getHierarchyLevel()) {
				current.setParent(previous);
			} else if (current.getHierarchyLevel() == previous.getHierarchyLevel()) {
				current.setParent(previous.getParent());
			} else {
				BalanceResponseDTO parent = previous.getParent();
				while (parent != null && parent.getHierarchyLevel() >= current.getHierarchyLevel()) {
					parent = parent.getParent();
				}
				current.setParent(parent);
			}

			CompanyAccount key = new CompanyAccount(current.getCompany(), current.getRazao());
			if (mapTerminationDocuments.containsKey(key)) {
				final BigDecimal sumValue = Optional.ofNullable(mapTerminationDocuments.get(key)).orElse(BigDecimal.ZERO);
				if (sumValue.signum() == 0) {
					return;
				}
				
				BalanceResponseDTO actual = current;
				while (actual != null) {
					actual.setCadocBalance(//
						Optional.ofNullable(actual.getCadocBalance()).orElse(BigDecimal.ZERO)
						.add(sumValue)//
					);
					actual = actual.getParent();
				}

			}
		}
		previousRef.set(current);
	}

	public List<Balance> getBalances(BalanceFilterDTO filterDTO) {
		return getBalances(filterDTO, null);
	}

	public List<Balance> getBalances(BalanceFilterDTO filterDTO, Function<Balances_, CqnPredicate> where) {
		return getBalances(filterDTO, -1, -1, where).listOf(Balance.class);
	}

	public Result getBalances(BalanceFilterDTO filterDTO, long skip, long top) {
		return getBalances(filterDTO, skip, top, null);
	}

	public Result getBalances(BalanceFilterDTO filterDTO, long skip, long top, Function<Balances_, CqnPredicate> where) {
		AtomicInteger index = new AtomicInteger(0); // Para controlar o índice geral
		AtomicReference<BalanceResponseDTO> previousRef = new AtomicReference<>();

		Map<CompanyAccount, BigDecimal> mapTerminationDocuments = getTerminationDocuments(filterDTO);

		List<Balance> result = new ArrayList<>();

		getBalanceStream(filterDTO, where)
				.forEach(current -> {
					processBalanceTermination(mapTerminationDocuments, current, previousRef);

					int currentIndex = index.getAndIncrement();
					if ((currentIndex >= skip && currentIndex < skip + top) || top == -1) {
						result.add(current);
					}

				});

		return ResultBuilder.selectedRows(result).inlineCount(index.incrementAndGet()).result();
	}

	private void loadTab1Bacen(BalanceFilterDTO filterDTO, Set<CompaniesWrapper> mapCompany)
			throws IOException, HttpException {
		BacenDTO bacenDTO = requestGetBacen(filterDTO.getCadoc().getPath(filterDTO, false));
		if (Objects.isNull(bacenDTO)) {
			return;
		}

		bacenDTO.getTab1().getCampos().parallelStream()//
				.filter(e -> Objects.nonNull(e.getEmpresa()) && !e.getEmpresa().isBlank())
				.forEach(//
						e -> mapCompany.add(//
								CompaniesWrapper.createCompany(null, null, e.getCnpj(), e.getEmpresa())//
						)//
				);

		bacenDTO.getTab3().getCampos().parallelStream()//
				.filter(e -> Objects.nonNull(e.getEmpresa()) && !e.getEmpresa().isBlank())

				.forEach(//
						e -> {
							e.setEmpresa(StringUtils.leftPad(e.getEmpresa(), 4, "0"));
							mapCompany.add(//
									CompaniesWrapper.createCompany(null, null, e.getCnpj(), e.getEmpresa())//
							);//
						});

		Set<String> mapExternalCodeCompany = ConcurrentHashMap.newKeySet();

		mapCompany.stream().map(e -> e.getExternalCode()).forEach(mapExternalCodeCompany::add);

		bacenDTO.setCadoc(filterDTO.getCadoc().getName());
		log.info("Mapping and saving balance data");
		List<Balances> balances = BacenMapper.INSTANCE.mapAllBalances(bacenDTO, mapExternalCodeCompany);

		db.run(Delete.from(Balances_.class).where(
			b -> b.company_externalCode().in(filterDTO.getCompanies()).and(
				b.cadoc().eq(filterDTO.getCadoc().getName()).and(
					b.exercise().eq(filterDTO.getExercise()).and(
						b.version().eq(filterDTO.getVersion()).and(
							b.interval().eq(filterDTO.getInterval())
						)
					)
				)
			)
		));

		db.run(Upsert.into(Balances_.class).entries(balances));
		balances = null;

		log.info("Mapping and saving outside balance data");
		List<OutsideBalance> outsideBalances = BacenMapper.INSTANCE.mapAllOusideBalances(bacenDTO,
				mapExternalCodeCompany);

		db.run(Delete.from(OutsideBalance_.class).where(
			b -> b.company_externalCode().in(filterDTO.getCompanies()).and(
				b.cadoc().eq(filterDTO.getCadoc().getName()).and(
					b.exercise().eq(filterDTO.getExercise()).and(
						b.version().eq(filterDTO.getVersion()).and(
							b.interval().eq(filterDTO.getInterval())
						)
					)
				)
			)
		));
		db.run(Upsert.into(OutsideBalance_.class).entries(outsideBalances));
	}

	private void loadTab2Bacen(BalanceFilterDTO filterDTO, Set<CompaniesWrapper> mapCompany)
			throws IOException, HttpException {
		BacenDTO bacenDTO = requestGetBacen(filterDTO.getCadoc().getPath(filterDTO, true));
		if (Objects.isNull(bacenDTO)) {
			return;
		}
		bacenDTO.setCadoc(filterDTO.getCadoc().getName());

		Set<String> mapExternalCodeCompany = ConcurrentHashMap.newKeySet();

		mapCompany.stream().map(e -> e.getExternalCode()).forEach(mapExternalCodeCompany::add);
		log.info("Mapping and saving termination data");

		ConcurrentLinkedQueue<TerminationDocument> docsBuffer = new ConcurrentLinkedQueue<>();
		db.run(Delete.from(TerminationDocument_.class).where(
			t -> t.company_externalCode().in(filterDTO.getCompanies()).and(
				t.exercise().eq(filterDTO.getExercise()).and(
					t.interval().eq(filterDTO.getInterval()).and(
						t.cadoc().eq(filterDTO.getCadoc().getName()).and(
							t.tpDoc().in(filterDTO.getTipoDoc()).and(
								t.dtLanc().eq(filterDTO.getReleaseDate())
							)
						)
					)
				)
			)
		));
		bacenDTO.getTab2().getCampos().parallelStream().forEach(e -> {

			TerminationDocument doc = BacenMapper.INSTANCE.toTerminationDocumentsEntity(e, mapExternalCodeCompany);
			doc.setCompanyExternalCode(doc.getCompany().getExternalCode());
			doc.setCadoc(bacenDTO.getCadoc());
			doc.setDtLanc(bacenDTO.getDtLanc());
			doc.setTpDoc(filterDTO.getTipoDoc().get(0));

			docsBuffer.add(doc);

			synchronized (docsBuffer) {
				if (docsBuffer.size() >= 1000) {
					if (!docsBuffer.isEmpty()) {
						db.run(Upsert.into(TerminationDocument_.class).entries(docsBuffer));
						docsBuffer.clear();
					}
				}
			}
		});

		if (!docsBuffer.isEmpty()) {
			synchronized (docsBuffer) {
				db.run(Upsert.into(TerminationDocument_.class).entries(docsBuffer));
				docsBuffer.clear();
			}
		}
	}

	private BacenDTO requestGetBacen(String path) throws IOException, HttpException {
		Optional<WrapperResultDTO<BacenDTO>> optBacenDTO = apiDestinationService.get(//
				path, //
				propertiesService.getCloudDestinationApi(), //
				new TypeReference<WrapperResultDTO<BacenDTO>>() {
				},
				code -> code == 200 //
		);

		if (optBacenDTO.isEmpty() || optBacenDTO.get().getResults() == null
				|| optBacenDTO.get().getResults().isEmpty()) {
			log.warn("Response is empty. Status 200 without content");
			return null;
		}

		return optBacenDTO.get().getResults().get(0);
	}

	public record CompanyAccount(
			String code, String account) {
	};
}
