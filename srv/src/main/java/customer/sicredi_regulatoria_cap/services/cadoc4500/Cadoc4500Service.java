package customer.sicredi_regulatoria_cap.services.cadoc4500;

import java.io.IOException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sap.cds.Result;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;

import customer.sicredi_regulatoria_cap.services.bacen.ReadCadoc4500Service;
import customer.sicredi_regulatoria_cap.services.bacen.SendBacenCadoc4500Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class Cadoc4500Service {

	@Autowired
	private ReadCadoc4500Service readCadoc4500Service;

	@Autowired
	private SendBacenCadoc4500Service sendBacenCadoc4500Service;

	public Cadoc4500BacenResponse transmitBacen(Cadoc4500FilterDTO filterDTO, boolean updateDocumentsBacen, String user, String remittanceType) {
		try {
			sendBacenCadoc4500Service.sendBacen(filterDTO, user, remittanceType);
			return new Cadoc4500BacenResponse("Accounts sent", true, false);
		} catch (UnsupportedCharsetException | IOException | HttpException e) {
			log.error("Error to send Bacen Cadoc 4500", e);
			throw new ServiceException(ErrorStatuses.SERVER_ERROR, e.getMessage());
		}
	}

	public Result readCadoc4500(Map<String, Object> where, CqnSelect select) {
		Cadoc4500FilterDTO filterDTO = new Cadoc4500FilterDTO(where);

		try {
			readCadoc4500Service.load(filterDTO);
		} catch (IOException | HttpException e) {
			log.error("Error to load Cadoc 4500", e);
			throw new ServiceException(ErrorStatuses.SERVER_ERROR, e.getMessage());
		}
		return readCadoc4500Service.read(filterDTO, select);
	}

	
	public record Cadoc4500FilterDTO(List<String> companies, String exercise, String interval) {
		public Cadoc4500FilterDTO(Map<String, Object> where) {
			this(Arrays.asList(where.get("company.externalCode").toString().split(",", -1)), (String)where.get("exercise"), (String)where.get("interval"));
		}

		public Cadoc4500FilterDTO(Collection<String> companies, String exercise, String interval) {
			this(new ArrayList<>(companies), exercise, interval);
		}
	}

	public record Cadoc4500BacenResponse(String message, boolean sent, boolean userConfirmation) {}
}
