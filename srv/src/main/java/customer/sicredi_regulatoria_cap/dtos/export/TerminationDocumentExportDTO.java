package customer.sicredi_regulatoria_cap.dtos.export;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;

@Data
public class TerminationDocumentExportDTO {

	@CsvBindByName(column = "Código")
	private String companyCode;

	@CsvBindByName(column = "Empresa")
	private String company;

	@CsvBindByName(column = "Documento")
	private String document;
	
	@CsvBindByName(column = "Exercício")
	private String exercise;
	
	@CsvBindByName(column = "Período")
	private String interval;
	
	@CsvBindByName(column = "Item")
	private String item;
	
	@CsvBindByName(column = "Montante")
	private Double amount;
	
	@CsvBindByName(column = "Razão")
	private String razao;
}
