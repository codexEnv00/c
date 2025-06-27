package customer.sicredi_regulatoria_cap.dtos.bacen;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonRootName("documento")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendBacenDTO {
	
	@JsonProperty("contas")
	private List<AccountDTO> accounts;

	@JsonProperty("codigoDocumento")
	private String cadoc;

	@JsonProperty("cnpj")
	private String cnpj;

	@JsonProperty("dataBase")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
	private LocalDate baseDate;

	@JsonProperty("tipoRemessa")
	private String remittanceType;

	@Data
	public static class AccountDTO {

		@JsonProperty("codigoConta")
		private String accountCode;

		@JsonProperty("saldo")
		private String balance;
	}
}
