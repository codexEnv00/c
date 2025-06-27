package customer.sicredi_regulatoria_cap.services.bacen;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cds.ql.Select;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.sicredi.db.entities.Companies;
import cds.gen.sicredi.db.entities.ProtocolDetails;
import cds.gen.sicredi.db.entities.Protocols;
import cds.gen.sicredi.db.entities.Protocols_;
import cds.gen.sicredi.db.entities.cadoc4500.Estban;
import customer.sicredi_regulatoria_cap.dtos.bacen.SendBacen4500DTO;
import customer.sicredi_regulatoria_cap.dtos.bacen.SendBacen4500DTO.Agency;
import customer.sicredi_regulatoria_cap.dtos.bacen.SendBacenBaseDTO;
import customer.sicredi_regulatoria_cap.repositories.EstbanCadoc4500Repository;
import customer.sicredi_regulatoria_cap.services.APIDestinationService;
import customer.sicredi_regulatoria_cap.services.CompanyService;
import customer.sicredi_regulatoria_cap.services.PropertiesService;
import customer.sicredi_regulatoria_cap.services.ReportService;
import customer.sicredi_regulatoria_cap.services.cadoc4500.Cadoc4500Service;
import customer.sicredi_regulatoria_cap.services.cadoc4500.Cadoc4500Service.Cadoc4500FilterDTO;

@Service
public class SendBacenCadoc4500Service extends SendBacenService<Cadoc4500Service.Cadoc4500FilterDTO, SendBacen4500DTO> {

	public SendBacenCadoc4500Service(ObjectMapper mapper, PersistenceService db, ReportService reportService,
			APIDestinationService apiDestinationService, PropertiesService propertiesService, CompanyService companyService) {
		super(mapper, db, reportService, apiDestinationService, propertiesService, "4500", companyService);
	}

	@Autowired
	private EstbanCadoc4500Repository repository;

	@Autowired
	private ReportService reportService;

	@Override
	protected String getInterval(Cadoc4500FilterDTO filterDTO) {
		return filterDTO.interval();
	}

	@Override
	protected String getExercise(Cadoc4500FilterDTO filterDTO) {
		return filterDTO.exercise();
	}

	@Override
	protected List<String> getCompanies(Cadoc4500FilterDTO filterDTO) {
		return filterDTO.companies();
	}

	@Override
	protected SendBacen4500DTO getBacenData(SendBacenBaseDTO bacenDTO, Cadoc4500FilterDTO filterDTO,
			Companies company) {
		List<Estban> estbans = getEstbans(filterDTO, company.getExternalCode());

		if (estbans.isEmpty()) {
			return null;
		}

		String agency = "<>";
		List<SendBacen4500DTO.Agency> agencies = new ArrayList<>();
		for (Estban estban : estbans) {
			if (!estban.getAgency().equals(agency)) {
				agency = estban.getAgency();
				agencies.add(new Agency(new ArrayList<>(), agency));
			}
			agencies.getLast().accounts().add(
					new SendBacen4500DTO.Account(estban.getAccount(), estban.getBalance().toString()));
		}

		SendBacen4500DTO sendDTO = SendBacen4500DTO.toBuilder(bacenDTO)
				.agencies(agencies)
				.cadoc("4500")
				.build();

		return sendDTO;
	}

	@Override
	protected void saveBacenAccounts(Cadoc4500FilterDTO filterDTO, Companies company, Protocols protocol) {
		final List<Estban> estbans = getEstbans(filterDTO, company.getExternalCode());

		final var details = IntStream.range(0, estbans.size())
		.mapToObj(i -> {
			var b = estbans.get(i);
			ProtocolDetails detail = ProtocolDetails.create();
			
			detail.setCadoc(protocol.getCadoc());
			detail.setCadocBalance(b.getBalance());
			detail.setProtocol(protocol);
			detail.setProtocolId(protocol.getId());
			detail.setCompanyExternalCode(company.getExternalCode());
			detail.setOrder(i + 1);

			detail.setCompany(company);
			detail.setCosifAccount(b.getAccount());
			detail.setDescription(b.getDescription());
			detail.setDrillState(b.getDrillState());
			detail.setExercise(filterDTO.exercise());
			detail.setInterval(filterDTO.interval());
			detail.setLevel(1);
			detail.setNodeID(b.getNodeID());
			detail.setRazaoAccount(b.getAccount());
			detail.setVersion("");

			return detail;
		}).toList();
 		
		reportService.addDetails(details);
	}

	@Override
	protected String getCIBacenPath() {
		return "/http/Cap_In_Bacen_DataTransfer4500";
	}

	@Override
	protected Select<Protocols_> getSelectDocumentExists(Cadoc4500FilterDTO filterDTO) {
		return Select.from(Protocols_.class)
				.where(
						p -> p.company_externalCode().in(filterDTO.companies()).and(
								p.exercise().eq(filterDTO.exercise()).and(
										p.interval().eq(filterDTO.interval()).and(
												p.cadoc().eq("4500")//
										)//
								)//
						)//
				);
	}

	private List<Estban> getEstbans(Cadoc4500FilterDTO filterDTO, String externalCode) {
		Select query = repository.buildSelectWithWhere(filterDTO)//
			.orderBy(o -> o.agency().asc());

		return repository.findAllBySelect(query).listOf(Estban.class);
	}
}
