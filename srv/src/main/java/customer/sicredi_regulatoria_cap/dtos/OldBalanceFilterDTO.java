package customer.sicredi_regulatoria_cap.dtos;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class OldBalanceFilterDTO {

	private String company;
	private String interval;
	private String exercise;

	@Pattern(regexp = "^(4016|4010)?$")
	private String cadoc;

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate startDate = LocalDate.now();

	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
	private LocalDate endDate = LocalDate.now();
}
