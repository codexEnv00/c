package customer.sicredi_regulatoria_cap.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceDTO {

	private String company;
	private String cnpj;
	private String cosif;
	private String razao;
	private String description;
	private Double cadocBalance;
}
