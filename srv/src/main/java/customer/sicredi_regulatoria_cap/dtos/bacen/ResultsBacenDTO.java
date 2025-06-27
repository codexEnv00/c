package customer.sicredi_regulatoria_cap.dtos.bacen;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.Data;


@Data
@JsonRootName("d")
public class ResultsBacenDTO {
	private List<BacenDTO> results;

}