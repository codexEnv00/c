package customer.sicredi_regulatoria_cap.dtos.balance;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TerminationDocumentsDTO {
   
	@JsonProperty("results") 
	private List<TerminationDocumentsFieldsDTO> campos;
		
	@Data
	public static class TerminationDocumentsFieldsDTO {
		@JsonProperty("Empresa") 
		private String empresa;
		
		@JsonProperty("Documento") 
		private String documento;
		
		@JsonProperty("Exercicio") 
		private String exercicio;
		
		@JsonProperty("Periodo") 
		private String periodo;
		
		@JsonProperty("Item") 
		private int item;
		
		@JsonProperty("Montante") 
		private double montante;
		
		@JsonProperty("Razao") 
		private String razao;

		@JsonProperty("TipoDoc")
		private String tpDoc;
	}
}