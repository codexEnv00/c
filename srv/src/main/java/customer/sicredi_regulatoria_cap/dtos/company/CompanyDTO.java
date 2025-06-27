package customer.sicredi_regulatoria_cap.dtos.company;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CompanyDTO {

    @JsonProperty("Empresa")
    private String externalCode;

    @JsonProperty("DescrEmpresa")
    private String description;

    @JsonProperty("CNPJ")
    private String cnpj;
}
