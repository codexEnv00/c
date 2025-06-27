package customer.sicredi_regulatoria_cap.dtos.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VersaoDTO {

    @JsonProperty("Versn")
    private String Versn;

    @JsonProperty("Vstxt")
    private String Vstxt;
}
