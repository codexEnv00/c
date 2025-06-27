package customer.sicredi_regulatoria_cap.dtos.bacen;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Estban4500DTO {

    @JsonProperty("Empresa")
    private String company;

    @JsonProperty("Cnpj")
    private String cnpj;

    @JsonProperty("Periodo")
    private String interval;

    @JsonProperty("Exercicio")
    private String exercise;

    @JsonProperty("Agencia")
    private String agency;

    @JsonProperty("Conta")
    private String account;

    @JsonProperty("Saldo")
    private String balance;

    @JsonProperty("Descricao")
    private String description;

    @JsonProperty("Nivel")
    private Integer level;

    @JsonIgnore
    public String getKey() {
        return company + "|" + interval + "|" + exercise + "|" + account + "|" + agency + "|" + level;
    }
}
