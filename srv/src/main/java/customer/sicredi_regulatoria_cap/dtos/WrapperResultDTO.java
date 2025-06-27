package customer.sicredi_regulatoria_cap.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonRootName("d")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WrapperResultDTO<T> {

	private List<T> results;
}
