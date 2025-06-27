package customer.sicredi_regulatoria_cap.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TerminateDocumentDTO {

	private String company;
	private String document;
	private String exercise;
	private String interval;
	private String item;
	private Double amount;
	private String reason;
}
