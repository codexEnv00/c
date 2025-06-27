package customer.sicredi_regulatoria_cap.dtos;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ReportFilterDTO {

	@Pattern(regexp = "^(4016|4010)?$")
	private String cadoc;
	private List<String> companies = new ArrayList<>(0);
	
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate startDate = LocalDate.now();

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate endDate = LocalDate.now();
}
