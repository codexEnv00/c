package customer.sicredi_regulatoria_cap.dtos.bacen;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@JsonRootName("documento")
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class SendBacenBaseDTO {

    @JsonProperty("codigoDocumento")
	private String cadoc;

	@JsonProperty("cnpj")
	private String cnpj;

	@JsonProperty("dataBase")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM")
	private LocalDate baseDate;

	@JsonProperty("tipoRemessa")
	private String remittanceType;

	public abstract static class SendBacenBaseDTOBuilder<C extends SendBacenBaseDTO, B extends SendBacenBaseDTO.SendBacenBaseDTOBuilder<C, B>> {
        protected B $fillValuesFromParent(SendBacenBaseDTO instance) {
            $fillValuesFromInstanceIntoBuilder(instance, this);
            return self();
        }
    }

}
