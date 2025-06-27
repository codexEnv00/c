package customer.sicredi_regulatoria_cap.dtos.bacen;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseSendBacenDTO {

    @JsonProperty("protocolo")
    private String protocol;
}
