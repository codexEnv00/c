package customer.sicredi_regulatoria_cap.handlers;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.CdsData;
import com.sap.cds.Result;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.StructuredType;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.EventContext;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CdsUpdateEventContext;
import com.sap.cds.services.cds.CdsUpsertEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.configservice.Companies_;
import cds.gen.configservice.ConfigService_;
import cds.gen.configservice.TipoDoc_;
import cds.gen.configservice.Versao_;
import cds.gen.sicredi.db.entities.Companies;
import customer.sicredi_regulatoria_cap.services.CompanyService;
import customer.sicredi_regulatoria_cap.services.ConfigService;
import lombok.extern.slf4j.Slf4j;

@Component
@ServiceName(ConfigService_.CDS_NAME)
@Slf4j
public class ConfigServiceHandler implements EventHandler {


	@Autowired
	private PersistenceService db;

	@Autowired
	private CompanyService companyService;

	@Autowired
	private ConfigService configService;

	@Before(entity = Companies_.CDS_NAME, event = { CqnService.EVENT_CREATE, CqnService.EVENT_DELETE })
	public void onBeforeCompanyCreateDeleteUpsert(EventContext context) {
		throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Event " + context.getEvent() + " is invalid");
	}

	@Before(entity = Companies_.CDS_NAME, event = CqnService.EVENT_UPDATE)
	public void onBeforeCompanyUpdate(CdsUpdateEventContext context) {
		var data = context.getCqn().data();
		if (data.size() != 2 || data.get("visibility") == null || data.get("externalCode") == null) {
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, "It just possible to edit visibility field");
		}
	}

	@Before(entity = Companies_.CDS_NAME, event = CqnService.EVENT_UPSERT)
	public void onBeforeCompanyUpsert(CdsUpsertEventContext context) {
		var data = context.getCqn().entries();

		var companiesCode = new Stack<String>();

		for (Map<String,Object> entity : data) {
			if (entity.size() != 2 || entity.get("visibility") == null || entity.get("externalCode") == null) {
				throw new ServiceException(ErrorStatuses.BAD_REQUEST, "It just possible to edit visibility field. Element: " + entity);
			}
			companiesCode.add(entity.get("externalCode").toString());
		}


		long results = db.run(
			Select.from(Companies_.class).where(c -> c.externalCode().in(companiesCode))
		).rowCount();

		if (results != data.size()) {
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Some companies does not exists in database");
		}
	}

	@On(entity = cds.gen.configservice.Companies_.CDS_NAME, event = CqnService.EVENT_READ)
	public void onReadCompany(CdsReadEventContext context) {
		try {
			companyService.loadCompanies();
		} catch (IOException | HttpException e) {
			log.error("Failed to load companies", e);
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Failed to load companies");
		}
		
		var select = context.getCqn();
		if (select.where().isEmpty()) {
			context.setResult(
				db.run(
					Select.from(cds.gen.sicredi.db.entities.Companies_.class)//
					.columns(select.items())//
					.where(c -> c.visibility().is(Boolean.TRUE))//
					.orderBy(select.orderBy())//
				)
			);
		} else {
			context.setResult(db.run(context.getCqn()));
		}
	}

	@On(entity = TipoDoc_.CDS_NAME, event = CqnService.EVENT_READ)
	public void onReadTipoDoc(CdsReadEventContext context) {
		try {
			context.setResult(configService.getTipoDocs());
		} catch (IOException | HttpException e) {
			log.error("Failed to load TipoDocs", e);
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Failed to load TipoDocs");
		}
	}

	@On(entity = Versao_.CDS_NAME, event = CqnService.EVENT_READ)
	public void onReadVersao(CdsReadEventContext context) {
		try {
			context.setResult(configService.getVersoes());
		} catch (IOException | HttpException e) {
			log.error("Failed to load Versao", e);
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Failed to load Versao");
		}
	}

	public <T extends StructuredType<T>> void process(Map<String, Optional<String>> map, Class<T> entity) {
		process(map, entity, "name");
	}

	public <T extends StructuredType<T>> void process(Map<String, Optional<String>> map, Class<T> entity, String columnName) {
		Result existing = db.run(
				Select.from(entity)//
						.where(//
								s -> s.get(columnName).in(
										map.keySet().stream().collect(Collectors.toList())//
								)//
						)//
		);

		existing.stream().parallel()
				.forEach(s -> {
					if (map.containsKey(s.get(columnName).toString()))
						map.put(s.get(columnName).toString(), Optional.of(s.get("ID").toString()));
				});

		List<CdsData> newRegisters = map.entrySet().stream()
				.filter(s -> s.getValue().isEmpty())
				.map(s -> {
					CdsData data = null;
					if (entity.equals(ConfigService_.COMPANIES)) {
						data = Companies.create();
					}

					data.put(columnName, s.getKey());

					return data;
				})
				.collect(Collectors.toList());

		Result registersInserted = db.run(//
				Insert.into(entity)//
						.entries(newRegisters)//
		);

		registersInserted.stream()//
				.forEach(r -> {
					map.put(r.get(columnName).toString(), Optional.of(r.get("ID").toString()));
				});
	}
}
