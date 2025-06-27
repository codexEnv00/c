package customer.sicredi_regulatoria_cap.dtos.bacen;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import customer.sicredi_regulatoria_cap.configs.deserializers.CustomLocalDateTimeDeserializerConfig;
import customer.sicredi_regulatoria_cap.dtos.balance.BalanceExtractionDTO;
import customer.sicredi_regulatoria_cap.dtos.balance.OutsideBalancesDTO;
import customer.sicredi_regulatoria_cap.dtos.balance.TerminationDocumentsDTO;
import lombok.Data;

@Data
public class BacenDTO {
	@JsonProperty("Empresa") 
	private String empresa;
	
	@JsonProperty("Versao") 
	private String versao;
	
	@JsonProperty("Periodo") 
	private String periodo;
	
	@JsonProperty("Exercicio") 
	private String exercicio;

	@JsonProperty("DtLanc")
	@JsonDeserialize(using = CustomLocalDateTimeDeserializerConfig.class)
	private LocalDate dtLanc;
	
	@JsonProperty("to_tab1")
	private BalanceExtractionDTO tab1;

	@JsonProperty("to_tab2")
	private TerminationDocumentsDTO tab2;

	@JsonProperty("to_tab3")
	private OutsideBalancesDTO tab3;

	@JsonIgnore
	private String cadoc;
}
