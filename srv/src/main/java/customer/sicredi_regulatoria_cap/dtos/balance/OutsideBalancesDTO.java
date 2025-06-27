package customer.sicredi_regulatoria_cap.dtos.balance;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OutsideBalancesDTO {
    
	@JsonProperty("results") 
	private List<OutsideBalancesFieldsDTO> campos;

	@Data
	public static class OutsideBalancesFieldsDTO {
		@JsonProperty("Empresa") 
		private String empresa;
		
		@JsonProperty("ContaRazao") 
		private String contaRazao;
		
		@JsonProperty("Descricao") 
		private String descricao;
		
		@JsonProperty("SaldoCadoc") 
		private double saldoCadoc;

		@JsonProperty("cnpj")
		private String cnpj;
	}
}