package customer.sicredi_regulatoria_cap.services.bacen;

import java.io.IOException;
import java.util.Optional;

import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sap.cds.Result;
import com.sap.cds.ResultBuilder;
import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Upsert;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.sicredi.db.entities.cadoc4500.Estban_;
import customer.sicredi_regulatoria_cap.dtos.WrapperResultDTO;
import customer.sicredi_regulatoria_cap.dtos.bacen.Estban4500DTO;
import customer.sicredi_regulatoria_cap.dtos.balance.BalanceFilterDTO;
import customer.sicredi_regulatoria_cap.mappers.cadoc4500.Estban4500Mapper;
import customer.sicredi_regulatoria_cap.repositories.EstbanCadoc4500Repository;
import customer.sicredi_regulatoria_cap.services.APIDestinationService;
import customer.sicredi_regulatoria_cap.services.BacenService;
import customer.sicredi_regulatoria_cap.services.CompanyService;
import customer.sicredi_regulatoria_cap.services.PropertiesService;
import customer.sicredi_regulatoria_cap.services.cadoc4500.Cadoc4500Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReadCadoc4500Service {

	@Autowired
	private APIDestinationService apiService;

	@Autowired
	private PropertiesService propertiesService;

	@Autowired
	private CompanyService companyService;

	@Autowired
	private PersistenceService db;

	@Autowired
	private EstbanCadoc4500Repository repository;

	public Result read(Cadoc4500Service.Cadoc4500FilterDTO filterDTO, CqnSelect select) {
		Result result = repository.findAllBySelect(
			repository.buildSelectWithWhereAndCount(filterDTO, select.top(), select.skip())
			.columns(
				e -> e._all(),
				e -> e.company().expand(),
				e -> e.level().as("hierarchyLevel"),
				e -> e.parentKey().as("parentNodeID")
			)
			.search(select.search().orElse(null))
		);
		return ResultBuilder.selectedRows(result.list()).inlineCount(result.inlineCount()).result();
	}

	public void load(Cadoc4500Service.Cadoc4500FilterDTO filterDTO) throws IOException, HttpException {
		companyService.loadCompanies();

		Optional<WrapperResultDTO<Estban4500DTO>> optBacenDTO = apiService.get(//
				BacenService.ECadoc.C4500.getPath(
						BalanceFilterDTO.builder()
								.companies(filterDTO.companies())
								.exercise(filterDTO.exercise())
								.interval(filterDTO.interval())
								.build(),
						false//
				), //
				propertiesService.getCloudDestinationApi(), //
				new TypeReference<WrapperResultDTO<Estban4500DTO>>() {
				},
				code -> code == 200 //
		);

		if (optBacenDTO.isEmpty() || optBacenDTO.get().getResults() == null
				|| optBacenDTO.get().getResults().isEmpty()) {
			log.warn("Response is empty. Status 200 without content");
			return;
		}

		db.run(
			Delete.from(Estban_.class)
			.where(
				e -> e.company_externalCode().in(filterDTO.companies()).and(
					e.interval().eq(filterDTO.interval()).and(
						e.exercise().eq(filterDTO.exercise())
					)
				)
			)
		);

		final Estban4500Mapper mapper = Estban4500Mapper.INSTANCE;
		db.run(//
				Upsert.into(Estban_.class).entries(
						mapper.mapAll(optBacenDTO.get().getResults())//
				)//
		);
	}

}
