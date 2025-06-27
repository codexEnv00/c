package customer.sicredi_regulatoria_cap.handlers;

import java.io.IOException;
import java.io.OutputStream;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.opencsv.exceptions.CsvException;
import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Predicate;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.AnalysisResult;
import com.sap.cds.ql.cqn.CqnAnalyzer;
import com.sap.cds.ql.cqn.CqnPredicate;
import com.sap.cds.ql.cqn.CqnStatement;
import com.sap.cds.reflect.CdsModel;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.reportservice.ProtocolData;
import cds.gen.reportservice.ProtocolData_;
import cds.gen.reportservice.ProtocolDetailsHeader;
import cds.gen.reportservice.ProtocolDetailsHistorical;
import cds.gen.reportservice.ReportService_;
import cds.gen.sicredi.db.entities.ProtocolBacen;
import cds.gen.sicredi.db.entities.ProtocolBacenError;
import cds.gen.sicredi.db.entities.ProtocolBacenError_;
import cds.gen.sicredi.db.entities.ProtocolBacenStatus_;
import cds.gen.sicredi.db.entities.ProtocolBacen_;
import cds.gen.sicredi.db.entities.ProtocolDetails;
import cds.gen.sicredi.db.entities.ProtocolDetails_;
import cds.gen.sicredi.db.entities.Protocols_;
import customer.sicredi_regulatoria_cap.dtos.ReportDTO;
import customer.sicredi_regulatoria_cap.dtos.ReportFilterDTO;
import customer.sicredi_regulatoria_cap.mappers.ReportMapper;
import customer.sicredi_regulatoria_cap.services.SheetService;
import customer.sicredi_regulatoria_cap.services.bacen.ReadBacenProtocol;

@Component
@ServiceName(ReportService_.CDS_NAME)
public class ReportServiceHandler implements EventHandler {

	@Autowired
	private SheetService sheetService;

	@Autowired
	private PersistenceService db;

	@Autowired
	private ReadBacenProtocol readBacenProtocol;

	@On(event = CqnService.EVENT_READ, entity = cds.gen.reportservice.Protocols_.CDS_NAME)
	public void onReadProtocols(CdsReadEventContext context) {
		if (context.getCqn().orderBy().size() == 1) {
			var cqn = context.getCqn();
			context.setResult(
				db.run(
					Select.from(Protocols_.class)
					.where(cqn.where().orElse(null))
					.columns(cqn.items())
					.orderBy(e -> e.createdAt().desc())
				).list()
			);

			return;
		}

		context.setResult(db.run(context.getCqn()));
	}

	@On(event = CqnService.EVENT_READ, entity = ProtocolData_.CDS_NAME)
	public void onReadProtocolData(CdsReadEventContext context) {
		CdsModel cdsModel = context.getModel();
		CqnAnalyzer cqnAnalyzer = CqnAnalyzer.create(cdsModel);

		CqnStatement cqn = context.getCqn();

		AnalysisResult result = cqnAnalyzer.analyze(cqn.ref());

		String protocol = (String) result.targetValues().get("header_protocol");
		if (protocol == null) {
			context.setResult(Arrays.asList());
			return;
		}

		try {
			readBacenProtocol.loadProtocolBacen(protocol);
		} catch (IOException | HttpException e) {}
		
		var protocolData = ProtocolData.create();
		protocolData.setAccounts(
			db.run(
				Select.from(ProtocolDetails_.class)
				.columns(
					p -> p._all(),
					p -> p.company().expand()
				)
				.where(
					c -> c.protocol_ID().eq(
						db.run(
							Select.from(Protocols_.class)
							.where(
								p -> p.protocol().eq(protocol)
							)
							.columns("ID")
						)
						.single()
						.get("ID")
						.toString()
					)
				)
			)
			.listOf(ProtocolDetails.class)
		);
		
		var protocolBacen = db.run(
			Select.from(
				ProtocolBacen_.class
			)
			.where(p -> p.protocol_ID().eq(protocol))
		).single(ProtocolBacen.class);

		protocolData.setHistorical(
			db.run(
				Select.from(ProtocolBacenStatus_.class)
				.where(
					p -> p.protocol_protocol_ID().eq(protocol)
					.and(
						CQL.not(p.responsableUnity().isNull())
					)
				)
			).listOf(ProtocolDetailsHistorical.class)
			.stream()
			.map(e -> {
				if (e.getCode() == 65) {
					e.setIsError(Boolean.TRUE);
				} else {
					e.setIsError(Boolean.FALSE);
				}
				return e;
			})
			.toList()
		);
		
		protocolData.setDetails(
			db.run(
				Select.from(ProtocolBacenError_.class)
				.where(p -> p.protocol_protocol_ID().eq(protocol))
			)
			.listOf(ProtocolBacenError.class)
		);

		var protocolHeader = ProtocolDetailsHeader.create();
		protocolHeader.setBaseDate(protocolBacen.getBaseDate());
		protocolHeader.setCadoc(protocolBacen.getCadoc());
		protocolHeader.setCnpj(protocolBacen.getCnpj());
		protocolHeader.setProtocol(protocol);
		protocolHeader.setSystem(protocolBacen.getSystem());

		protocolData.setHeader(protocolHeader);
		context.setResult(Arrays.asList(protocolData));
	}

	public void exportReportCSV(ReportFilterDTO filterDTO, OutputStream outputStream) throws IOException, CsvException {
		sheetService.exportCSV(outputStream, generateReport(filterDTO), ReportDTO.class);
	}

	public void exportReportXLSX(ReportFilterDTO filterDTO, OutputStream outputStream) throws IOException {
		sheetService.exportXLSX(outputStream, generateReport(filterDTO), ReportDTO.class, (ws, report, i) -> {
			ws.value(i, 0, report.getProtocol());
			ws.value(i, 1, report.getCompany());
			ws.value(i, 2, report.getCnpj());
			ws.value(i, 3, report.getCosif());
			ws.value(i, 4, report.getRazao());
			ws.value(i, 5, report.getDescription());
			ws.value(i, 6, report.getCadocBalance());
			ws.value(i, 7, report.getSegment());
			ws.value(i, 8, report.getStatus());
			ws.value(i, 9, report.getCadoc());
			ws.value(i, 10, report.getCreatedBy());

			ws.value(i, 11, report.getCreatedAt());
			ws.style(i, 11).format("dd/MM/yyyy H:mm:ss").set();

		});
	}

	private Stream<ReportDTO> generateReport(ReportFilterDTO filterDTO) {
		Function<ProtocolDetails_, CqnPredicate> where = e -> {
			Predicate predicate = e.protocol().createdAt().between(//
					filterDTO.getStartDate().atStartOfDay().toInstant(ZoneOffset.UTC), //
					filterDTO.getEndDate().atTime(23, 59, 59).toInstant(ZoneOffset.UTC)//
			);

			if (StringUtils.hasText(filterDTO.getCadoc())) {
				predicate = predicate.and(e.protocol().cadoc().eq(filterDTO.getCadoc()));
			}

			if (filterDTO.getCompanies() != null && !filterDTO.getCompanies().isEmpty()) {
				predicate = predicate.and(e.company_externalCode().in(filterDTO.getCompanies()));
			}

			return predicate;
		};

		return ReportMapper.INSTANCE.mapAll(
				db.run(//
						Select.from(//
								ProtocolDetails_.class//
						)//
								.columns(//
										i -> i.protocol().expand(
												p -> p._all()),
										i -> i.company().expand(),
										i -> i.company()._all())//
								.where(where)//
				)
						.streamOf(ProtocolDetails.class)//
		);
	}

}
