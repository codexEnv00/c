package customer.sicredi_regulatoria_cap.dtos;

import java.time.LocalDateTime;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import lombok.Data;

@Data
public class ReportDTO {

	@CsvBindByName(column = "Protocolo")
	private String protocol;

	@CsvBindByName(column = "Companhia")
	private String company;

	@CsvBindByName(column = "CNPJ")
	private String cnpj;

	@CsvBindByName(column = "Conta COSIF")
	private String cosif;

	@CsvBindByName(column = "Conta Razão")
	private String razao;

	@CsvBindByName(column = "Descrição")
	private String description;

	@CsvBindByName(column = "Saldo CADOC")
	private Double cadocBalance;

	@CsvBindByName(column = "Segmento")
	private String segment;

	@CsvBindByName(column = "Status")
	private String status;

	@CsvBindByName(column = "CADOC")
	private String cadoc;

	@CsvBindByName(column = "Criado por")
	private String createdBy;

	@CsvDate("dd/MM/yyyy hh:mm:ss")
	@CsvBindByName(column = "Criado em")
	private LocalDateTime createdAt;

}
