package customer.sicredi_regulatoria_cap.handlers.cadoc4500;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sap.cds.ql.cqn.CqnElementRef;
import com.sap.cds.ql.cqn.CqnLiteral;
import com.sap.cds.ql.cqn.CqnVisitor;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.Before;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;

import cds.gen.cadoc4500service.Cadoc4500Service_;
import cds.gen.cadoc4500service.Estban_;
import cds.gen.cadoc4500service.TransmitBacenContext;
import customer.sicredi_regulatoria_cap.services.cadoc4500.Cadoc4500Service;

@Component
@ServiceName(Cadoc4500Service_.CDS_NAME)
public class Cadoc4500ServiceHandler implements EventHandler {

	@Autowired
	private Cadoc4500Service cadoc4500Service;

	@Before(entity = Estban_.CDS_NAME, event = CqnService.EVENT_READ)
	public void onBeforeReadEstban(CdsReadEventContext context) {
		Set<String> keys = getTargetValues(context).keySet();

		if (!keys.contains("company.externalCode")) {
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Filter companies is required");
		}

		if (!keys.contains("exercise")) {
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Filter exercise is required");
		}

		if (!keys.contains("interval")) {
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Filter interval is required");
		}
	}

	@On(entity = Estban_.CDS_NAME)
	public void onReadEstban(CdsReadEventContext context) {
		context.setResult(cadoc4500Service.readCadoc4500(getTargetValues(context), context.getCqn()));
	}

	@On
	public void onTransmitBacen(TransmitBacenContext context) {
		if (Objects.isNull(context.getCompanies())) {
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Companies is required");
		}

		if (Objects.isNull(context.getExercise())) {
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Exercise is required");
		}

		if (Objects.isNull(context.getInterval())) {
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Interval is required");
		}

		var filterDTO = new Cadoc4500Service.Cadoc4500FilterDTO(context.getCompanies(), context.getExercise(), context.getInterval());
		var cadoc4500Response = cadoc4500Service.transmitBacen(filterDTO, true, context.getUser(), context.getRemittanceType());
		var transmitBacenReturn = TransmitBacenContext.ReturnType.create();

		transmitBacenReturn.setMessage(cadoc4500Response.message());
		transmitBacenReturn.setSent(cadoc4500Response.sent());
		transmitBacenReturn.setUserConfirmation(cadoc4500Response.userConfirmation());

		context.setResult(transmitBacenReturn);
		context.setCompleted();
	}

	private Map<String, Object> getTargetValues(CdsReadEventContext context) {
		final Map<String, Object> where = new HashMap<>();
		CqnVisitor visitor = new CqnVisitor() {
			String elementName;
			final Deque<String> stack = new ArrayDeque<>();

			@Override
			public void visit(CqnElementRef elementRef) {
				elementName = elementRef.path();
				stack.clear();
			}
			
			@Override
			public void visit(CqnLiteral<?> literal) {
				stack.add(literal.value().toString());
				where.put(elementName, String.join(",", stack));
			}
		};

		if (context.getCqn().where().isEmpty()) {
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, "$filter is required");
		}

		context.getCqn().where().get().accept(visitor);

		return where;
	}
}
