package customer.sicredi_regulatoria_cap.dtos.bacen;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonRootName("documento")
@SuperBuilder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class SendBacen4500DTO extends SendBacenBaseDTO {
	
	@JsonProperty("agencias")
	private List<Agency> agencies;

	public static record Agency (
		@JsonProperty("contas")    
		List<Account> accounts,

		@JsonProperty("idAgencia")
		String agencyCode
	) {};

	public static record Account (
		@JsonProperty("codigoConta")
		String code, 
		
		@JsonProperty("saldoContabil")
		String balance
	) {}


	public static SendBacen4500DTOBuilder<?, ?> toBuilder(SendBacenBaseDTO p) {
        return new SendBacen4500DTOBuilderImpl().$fillValuesFromParent(p);
    }
}
