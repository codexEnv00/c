package customer.sicredi_regulatoria_cap.dtos.export;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;

@Data
public class OutsideBalanceExportDTO {

	@CsvBindByName(column = "Empresa")
	private String company;

	@CsvBindByName(column = "CNPJ")
	private String cnpj;

	@CsvBindByName(column = "Conta Razão")
	private String razao;

	@CsvBindByName(column = "Descrição")
	private String description;

	@CsvBindByName(column = "Saldo CADOC")
	private Double cadocBalance;
}
