package customer.sicredi_regulatoria_cap.dtos.balance;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BalanceExtractionDTO {

    @JsonProperty("results") 
	private List<BalanceExtractionFieldsDTO> campos;

	@Data
	public static class BalanceExtractionFieldsDTO {
		@JsonProperty("Empresa") 
		private String empresa;
		
		@JsonProperty("ContaCosif") 
		private String contaCosif;
		
		@JsonProperty("ContaRazao") 
		private String contaRazao;
		
		@JsonProperty("Descricao") 
		private String descricao;
		
		@JsonProperty("SaldoCadoc") 
		private double saldoCadoc;
		
		@JsonProperty("Nivel")
		private int nivel;

		@JsonProperty("Cnpj")
		private String cnpj;

		@JsonIgnore
		public String getKey() {
			return empresa + "|" + cnpj + "|" + contaCosif + "|" + contaRazao + "|" + nivel;
		}
	}
}
