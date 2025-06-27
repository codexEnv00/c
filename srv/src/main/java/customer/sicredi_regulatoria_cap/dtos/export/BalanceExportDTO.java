package customer.sicredi_regulatoria_cap.dtos.export;

import java.math.BigDecimal;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;

@Data
public class BalanceExportDTO {

    @CsvBindByName(column = "Empresa")
    private String company;

    @CsvBindByName(column = "CNPJ")
    private String cnpj;

    @CsvBindByName(column = "COSIF")
    private String cosif;

    @CsvBindByName(column = "Descrição")
    private String description;

    @CsvBindByName(column = "Razão")
    private String razao;

    @CsvBindByName(column = "Saldo")
    private BigDecimal cadocBalance;

    @CsvBindByName(column = "Saldo Original")
    private BigDecimal originalCadocBalance;

    private Integer hierarchyLevel;

    private BalanceExportDTO parent;
}
