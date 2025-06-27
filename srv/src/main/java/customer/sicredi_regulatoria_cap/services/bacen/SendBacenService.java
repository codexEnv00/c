package customer.sicredi_regulatoria_cap.services.bacen;

import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.http.HttpException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.sicredi.db.entities.Companies;
import cds.gen.sicredi.db.entities.Companies_;
import cds.gen.sicredi.db.entities.Protocols;
import cds.gen.sicredi.db.entities.Protocols_;
import cds.gen.sicredi.db.entities.Status;
import customer.sicredi_regulatoria_cap.dtos.bacen.ResponseSendBacenDTO;
import customer.sicredi_regulatoria_cap.dtos.bacen.SendBacenBaseDTO;
import customer.sicredi_regulatoria_cap.dtos.balance.BalanceFilterDTO;
import customer.sicredi_regulatoria_cap.services.APIDestinationService;
import customer.sicredi_regulatoria_cap.services.CompanyService;
import customer.sicredi_regulatoria_cap.services.PropertiesService;
import customer.sicredi_regulatoria_cap.services.ReportService;
import customer.sicredi_regulatoria_cap.services.cadoc4500.Cadoc4500Service;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public abstract class SendBacenService<T, U extends SendBacenBaseDTO> {

	private ObjectMapper mapper;

	private PersistenceService db;

	private ReportService reportService;

	private APIDestinationService apiDestinationService;

	private PropertiesService propertiesService;

	private final String cadoc;
	
	private CompanyService companyService;

	public void sendBacen(T filterDTO, String user, String remittanceType) throws UnsupportedCharsetException, IOException, HttpException {
		mapper.findAndRegisterModules();
		mapper.disable(DeserializationFeature.UNWRAP_ROOT_VALUE);
		mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);

		List<Companies> companies = db.run(
				Select.from(Companies_.class)//
						.columns(//
								c -> c.cnpj(), //
								c -> c.externalCode())//
						.where(//
								c -> c.externalCode().in(getCompanies(filterDTO))//
						)//
		).listOf(Companies.class);

		final LocalDate baseDate = reportService.generateBaseDate(getInterval(filterDTO), getExercise(filterDTO));

		log.info("{} companies to send", companies.size());
		for (Companies company : companies) {
			Protocols protocol;
			if (filterDTO instanceof BalanceFilterDTO) {
				protocol = reportService.createReport((BalanceFilterDTO) filterDTO, company, user);
			}
			else if (filterDTO instanceof Cadoc4500Service.Cadoc4500FilterDTO) {
				protocol = reportService.createReport((Cadoc4500Service.Cadoc4500FilterDTO) filterDTO, company);
			}
			else {
				throw new ServiceException(ErrorStatuses.SERVER_ERROR, "Invalid FilterDTO type");
			}

			log.info("Cadoc {} Protocol {} for company {}", cadoc, protocol.getId(), company.getExternalCode());
			
			protocol.setCadoc(cadoc);
			protocol.setCreatedBy(user);

			SendBacenBaseDTO bacenDTO = SendBacenBaseDTO.builder()
				.baseDate(baseDate)
				.cnpj(company.getCnpj().substring(0, 8))//
				.baseDate(baseDate)//
				.remittanceType(remittanceType)
				.build();
			
			U bacenData = getBacenData(bacenDTO, filterDTO, company);
			if (Objects.isNull(bacenData)) {
				continue;
			}

			saveBacenAccounts(filterDTO, company, protocol);
			
			final StringEntity entity = new StringEntity(//
					mapper.writeValueAsString(bacenData),
					ContentType.APPLICATION_JSON//
			);

			var auth = companyService.getCompanyAuthenticator(company);

			String authBase64 = Base64.getEncoder().encodeToString(
				(auth.get().getUser() + ":" + auth.get().getPassword()).getBytes()
			);

			Optional<ResponseSendBacenDTO> optResponse = apiDestinationService.post(//
					getCIBacenPath(), //
					propertiesService.getCloudDestinationApi(), //
					entity, ResponseSendBacenDTO.class, //
					code -> code == 200, //
					mapper,
					new BasicHeader("Company-Authorization", authBase64)//
			);

			if (optResponse.isEmpty() || Objects.isNull(optResponse.get())) {
				throw new ServiceException(ErrorStatuses.SERVER_ERROR, "Response Send to Bacen is empty");
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

	public boolean areDocumentsSent(T filterDTO) {
		return db.run(getSelectDocumentExists(filterDTO)).rowCount() > 0;
	}
	
	protected abstract Select<Protocols_> getSelectDocumentExists(T filterDTO);

	protected abstract String getInterval(T filterDTO);
	protected abstract String getExercise(T filterDTO);
	protected abstract List<String> getCompanies(T filterDTO);

	protected abstract U getBacenData(SendBacenBaseDTO bacenDTO, T filterDTO, Companies company);

	protected void saveBacenAccounts(T filterDTO, Companies company, Protocols protocol) {}

	protected abstract String getCIBacenPath();
}
