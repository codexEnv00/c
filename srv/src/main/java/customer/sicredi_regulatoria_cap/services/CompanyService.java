package customer.sicredi_regulatoria_cap.services;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Upsert;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.sicredi.db.entities.Companies;
import cds.gen.sicredi.db.entities.Companies_;
import cds.gen.sicredi.db.entities.CompanyAuthenticators;
import cds.gen.sicredi.db.entities.CompanyAuthenticators_;
import customer.sicredi_regulatoria_cap.dtos.WrapperResultDTO;
import customer.sicredi_regulatoria_cap.dtos.company.CompanyDTO;
import customer.sicredi_regulatoria_cap.mappers.CompanyMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CompanyService {

	@Autowired
	private APIDestinationService apiDestinationService;

	@Autowired
	private PropertiesService propertiesService;

	@Autowired
	private PersistenceService db;

	public Optional<CompanyAuthenticators> getCompanyAuthenticator(Companies company) {
		var result = db.run(Select.from(CompanyAuthenticators_.class).where(c -> c.company_externalCode().eq(company.getExternalCode())));
		if (result.rowCount() == 0) {
			return Optional.empty();
		}

		return Optional.of(result.single(CompanyAuthenticators.class));
	}

	@Cacheable(value = "companies")
	public void loadCompanies() throws IOException, HttpException {
		Optional<WrapperResultDTO<CompanyDTO>> optWrapper = apiDestinationService.get(
				"/http/cadoc4010/getEmpresaSet?$format=json", //
				propertiesService.getCloudDestinationApi(), //
				new TypeReference<WrapperResultDTO<CompanyDTO>>() {
				},
				code -> code == 200);

		if (optWrapper.isEmpty()) {
			log.warn("Empty companies response. It is not a error, but it should be checked");
			return;
		}

		WrapperResultDTO<CompanyDTO> wrapper = optWrapper.get();

		Set<String> externalCodes = ConcurrentHashMap.newKeySet();
		wrapper.getResults().parallelStream()//
				.map(c -> c.getExternalCode())//
				.forEach(externalCodes::add);

		Set<String> existingCompanies = ConcurrentHashMap.newKeySet();

		db.run(
				Select.from(Companies_.class)
						.columns(c -> c.externalCode())
						.groupBy("externalCode")
						.where(c -> c.externalCode().in(externalCodes.toArray(new String[0]))))//
				.streamOf(Companies.class)//
				.parallel()//
				.map(e -> e.getExternalCode())
				.forEach(existingCompanies::add);

		db.run(
				Upsert.into(Companies_.class)
						.entries(
								wrapper.getResults().parallelStream()
										.map(e -> CompanyMapper.INSTANCE.toMap(e))
										.toList()//
						)//
		);
	}
}
