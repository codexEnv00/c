package customer.sicredi_regulatoria_cap.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sap.cds.Result;
import com.sap.cds.ql.Insert;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.balanceservice.Balance;
import cds.gen.sicredi.db.entities.Companies;
import cds.gen.sicredi.db.entities.ProtocolDetails;
import cds.gen.sicredi.db.entities.ProtocolDetails_;
import cds.gen.sicredi.db.entities.Protocols;
import cds.gen.sicredi.db.entities.Protocols_;
import cds.gen.sicredi.db.entities.Status;
import customer.sicredi_regulatoria_cap.dtos.bacen.SendBacenDTO.AccountDTO;
import customer.sicredi_regulatoria_cap.dtos.balance.BalanceFilterDTO;
import customer.sicredi_regulatoria_cap.mappers.BacenMapper;
import customer.sicredi_regulatoria_cap.mappers.ProtocolMapper;
import customer.sicredi_regulatoria_cap.services.cadoc4500.Cadoc4500Service;

@Service
public class ReportService {

	@Autowired
	private PersistenceService db;

	public Protocols createReport(Cadoc4500Service.Cadoc4500FilterDTO filterDTO, Companies company) {
		Protocols protocol = ProtocolMapper.INSTANCE.map(filterDTO);
		
		protocol.setCompany(company);
		protocol.setCompanyExternalCode(company.getExternalCode());
		
		return insertProtocol(protocol);
	}

	public Protocols createReport(BalanceFilterDTO filterDTO, Companies company, String user) {
		Protocols protocol = Protocols.create();
		
		protocol.setCadoc(filterDTO.getCadoc().getName());
		protocol.setStatus(Status.PROCESSING);
		protocol.setCompany(company);
		protocol.setCompanyExternalCode(company.getExternalCode());
		
		protocol.setExercise(filterDTO.getExercise());
		protocol.setVersion(filterDTO.getVersion());
		protocol.setInterval(filterDTO.getInterval());
		protocol.setTipoDoc(filterDTO.getTipoDoc().get(0));
		protocol.setReleaseDate(filterDTO.getReleaseDate());
		protocol.setBloc(filterDTO.getBloc() == null ? null : filterDTO.getBloc().getText());

		protocol.setCreatedBy(user);

		return insertProtocol(protocol);
	}

	private Protocols insertProtocol(Protocols protocol) {
		Result result = db.run(Insert.into(Protocols_.class).entry(protocol));
		if (!result.first().isPresent()) {
			throw new ServiceException(ErrorStatuses.SERVER_ERROR, "Failed to insert Protocol");
		}

		Optional<Protocols> optProtocol = result.first(Protocols.class);
		return optProtocol.get();
	}

	public void addDetails(List<ProtocolDetails> details) {
		db.run(
			Insert.into(ProtocolDetails_.class).entries(details)
		);
	}

	public List<AccountDTO> addDetails(Protocols protocol, Stream<Balance> balances, BalanceFilterDTO filterDTO) {
		Map<String, Companies> mapCompanyCode = new ConcurrentHashMap<>();

		List<AccountDTO> accounts = new ArrayList<>();

		AtomicInteger index = new AtomicInteger(0);

		List<ProtocolDetails> details = balances
		.filter(b -> b.getRazao() == null || b.getRazao().isBlank())
		.map(b -> {
			ProtocolDetails detail = ProtocolDetails.create();
			
			b.setCadocBalance(b.getCadocBalance());

			detail.setCadoc(protocol.getCadoc());
			detail.setCadocBalance(b.getCadocBalance());
			detail.setProtocolId(protocol.getId());
			detail.setCompanyExternalCode(b.get("company").toString());
			detail.setOrder(index.getAndIncrement());

			Companies company = Optional.ofNullable(mapCompanyCode.get(detail.getCompanyExternalCode())).orElseGet(() -> {
				Companies comp = Companies.create(detail.getCompanyExternalCode());
				comp.setCnpj(b.getCnpj());

				mapCompanyCode.put(detail.getCompanyExternalCode(), comp);
				return comp;
			});

			detail.setCompany(company);
			detail.setCosifAccount(b.getCosif());
			detail.setDescription(b.getDescription());
			detail.setDrillState(b.getDrillState());
			detail.setExercise(filterDTO.getExercise());
			detail.setInterval(filterDTO.getInterval());
			detail.setLevel(b.getHierarchyLevel());
			detail.setNodeID(b.getNodeID());
			detail.setRazaoAccount(b.getRazao());
			detail.setVersion(filterDTO.getVersion());

			accounts.add(BacenMapper.INSTANCE.mapSendBacen(b));
			
			return detail;
		}).toList();
 		db.run(
			Insert.into(ProtocolDetails_.class).entries(details)
		);

		return accounts;
	}

	public LocalDate generateBaseDate(String interval, String exercise) {
		Integer exerciseInt = Integer.parseInt(exercise);
		Integer intervalInt = Integer.parseInt(interval);
		intervalInt = intervalInt > 12 ? 12 : intervalInt;

		return LocalDate.of(exerciseInt, intervalInt, 1);
	}
}
