package customer.sicredi_regulatoria_cap.handlers;

import java.io.IOException;
import java.time.YearMonth;
import java.util.Optional;

import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.balanceservice.BalanceService_;
import cds.gen.balanceservice.Balance_;
import cds.gen.balanceservice.LoadBalanceContext;
import cds.gen.balanceservice.OutsideBalance_;
import cds.gen.balanceservice.StatusContext;
import cds.gen.balanceservice.TerminationDocument_;
import cds.gen.balanceservice.TransmitBacenContext;
import customer.sicredi_regulatoria_cap.dtos.balance.BalanceFilterDTO;
import customer.sicredi_regulatoria_cap.services.BacenService;
import customer.sicredi_regulatoria_cap.services.BacenService.BlocoMeses;
import customer.sicredi_regulatoria_cap.services.BacenService.ECadoc;
import customer.sicredi_regulatoria_cap.services.ParameterReadContextService;
import lombok.extern.slf4j.Slf4j;

@Component
@ServiceName(BalanceService_.CDS_NAME)
@Slf4j
public class BalanceServiceHandler implements EventHandler {

	@Autowired
	private BacenService bacenService;

	@Autowired
	private ParameterReadContextService parameterReadContextService;

	@Before(//
			event = CqnService.EVENT_READ, //
			entity = { //
					Balance_.CDS_NAME, TerminationDocument_.CDS_NAME, OutsideBalance_.CDS_NAME//
			}//
	)
	public void onBeforeReadBalance(CdsReadEventContext context) {

		parameterReadContextService.put(
				"cadoc", context, //
				(cadoc) -> ECadoc.getCadoc(cadoc).orElseThrow(//
						() -> //
						new ServiceException(ErrorStatuses.BAD_REQUEST, "Cadoc {} not found", cadoc//
						)//
				)//
		);

		if (ECadoc.C4016.equals((ECadoc) context.get("cadoc"))) {
			parameterReadContextService.put("blc", context, //
					c -> BlocoMeses.getFromText(c).orElseThrow(
							() -> new ServiceException(ErrorStatuses.BAD_REQUEST, "Blc {} not found", c)//
					),
					true);
		}

		parameterReadContextService.put("companies", context, c -> c.split(","), true);
		parameterReadContextService.put("exercise", context, c -> c, true);

		parameterReadContextService.put("version", context, c -> c, true);
		parameterReadContextService.put("interval", context, c -> {
			BlocoMeses blocoMeses = parameterReadContextService.get(context, "blc", BlocoMeses.class);
			var cadoc = parameterReadContextService.get(context, "cadoc", ECadoc.class);

			if (ECadoc.C4010.equals(cadoc)) {
				return c;
			}
			if (BlocoMeses.DEZ.equals(blocoMeses)) {
				return "12";
			}
			if (BlocoMeses.JUN.equals(blocoMeses)) {
				return "06";
			}
			return c;

		}, false);

		parameterReadContextService.put("tipoDoc", context, c -> {

			Integer month = Integer.parseInt(parameterReadContextService.get(context, "interval", String.class));

			if (month <= 6) {
				return new String[] {"F1"};
			} 
			return new String[] {"F2"};
		}, false);
		
		
		parameterReadContextService.put("releaseDate", context, c -> {
			String exercise = context.getParameterInfo().getQueryParameter("exercise");
			String interval = parameterReadContextService.get(context, "interval", String.class);

			return YearMonth.of(Integer.valueOf(exercise), Integer.valueOf(interval)).atEndOfMonth();
		}, false);

		parameterReadContextService.put("termination", context, c -> {
			if (c == null) {
				return null;
			}
			return c.split(",");
		}, false);
	}

	@On(event = CqnService.EVENT_READ, entity = Balance_.CDS_NAME)
	public void onReadBalance(CdsReadEventContext context) {
		BalanceFilterDTO filterDTO = BalanceFilterDTO.build(context);

		String nodeId = context.getParameterInfo().getQueryParameter("parentNodeID");

		log.info("Get Bacen Data for CADOC {}", filterDTO.getCadoc());
		context.setResult(bacenService.getBacen(filterDTO, context.getCqn(), nodeId));
		log.info("Response Bacen Data");
	}

	@On(event = CqnService.EVENT_READ, entity = TerminationDocument_.CDS_NAME)
	public void onReadTerminationDocument(CdsReadEventContext context) {
		BalanceFilterDTO filterDTO = BalanceFilterDTO.build(context);
		System.out.println(filterDTO);

		log.info("Get Termination Documents for CADOC {}", filterDTO.getCadoc());
		context.setResult(bacenService.getTerminationDocument(filterDTO, context.getCqn()));
	}

	@On(event = CqnService.EVENT_READ, entity = OutsideBalance_.CDS_NAME)
	public void onReadOutsideBalance(CdsReadEventContext context) {
		BalanceFilterDTO filterDTO = BalanceFilterDTO.build(context);

		log.info("Get Outside Balance for CADOC {}", filterDTO.getCadoc());
		context.setResult(bacenService.getOutsideBalances(filterDTO, context.getCqn()));
	}

	@On
	public void onLoadBalance(LoadBalanceContext context) throws IOException, HttpException {
		BalanceFilterDTO filterDTO = BalanceFilterDTO.build(context);
		log.info("Loading Balance");

		var id = bacenService.generateLoadBalanceAsyncId(filterDTO);
		var returnType = LoadBalanceContext.ReturnType.create();
		returnType.setId(id);

		context.setResult(returnType);
		context.setCompleted();
	}

	@On
	public void onStatus(StatusContext context) {
		if (context.getId() == null) {
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, "ID is required");
		}

		var status = bacenService.getStatus(context.getId());
		var returnType = StatusContext.ReturnType.create();
		returnType.setMessage(status.message());
		returnType.setStatus(status.status());

		context.setResult(returnType);
		context.setCompleted();
	}

	@On
	public void onTransmitBacen(TransmitBacenContext context) {
		BalanceFilterDTO filterDTO = BalanceFilterDTO.build(context);
		context.setUpdateDocumentsBacen(Optional.ofNullable(context.getUpdateDocumentsBacen()).orElse(Boolean.FALSE));
		log.info("Transmitting to Bacen");
		
		BacenService.SendBacenResponse response = bacenService.transmitToBacen(filterDTO, context.getUpdateDocumentsBacen(), context.getUser());
		TransmitBacenContext.ReturnType result = TransmitBacenContext.ReturnType.create();
		result.setMessage(response.message());
		result.setSent(response.sent());
		result.setUserConfirmation(response.userConfirmation());

		context.setResult(result);
		context.setCompleted();
	}
}
