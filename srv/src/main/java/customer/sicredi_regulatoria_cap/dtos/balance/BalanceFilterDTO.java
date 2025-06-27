package customer.sicredi_regulatoria_cap.dtos.balance;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.cds.CdsReadEventContext;

import cds.gen.balanceservice.LoadBalanceContext;
import cds.gen.balanceservice.TransmitBacenContext;
import customer.sicredi_regulatoria_cap.services.BacenService.BlocoMeses;
import customer.sicredi_regulatoria_cap.services.BacenService.ECadoc;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class BalanceFilterDTO {

	private ECadoc cadoc;
	private List<String> companies;
	private List<String> terminationCompanies;
	private String exercise;
	private String version;
	private String interval;
	private List<String> tipoDoc;
	private LocalDate releaseDate;
	private BlocoMeses bloc;
	private String remittanceType;

	public static BalanceFilterDTO build(LoadBalanceContext context) {
		var filterDTO = BalanceFilterDTO.builder()
				.cadoc(
						Optional.ofNullable(context.getCadoc())
								.flatMap(ECadoc::getCadoc)
								.orElseThrow(() -> new ServiceException(ErrorStatuses.BAD_REQUEST, "Invalid Cadoc")))
				.companies(new ArrayList<String>(context.getCompanies()))
				.exercise(context.getExercise())
				.interval(context.getInterval())
				.version(context.getVersion())
				.build();

		if (context.getBlc() != null) {
			filterDTO.setBlc(context.getBlc());
		}

		filterDTO.generateInterval();
		filterDTO.generateReleaseDate();
		filterDTO.generateTpDoc();

		return filterDTO;
	}

	public static BalanceFilterDTO build(TransmitBacenContext context) {
		var filterDTO = BalanceFilterDTO.builder()
				.cadoc(
						Optional.ofNullable(context.getCadoc())
								.flatMap(ECadoc::getCadoc)
								.orElseThrow(() -> new ServiceException(ErrorStatuses.BAD_REQUEST, "Invalid Cadoc")))
				.bloc(
						Optional.ofNullable(context.getBlc())
								.flatMap(BlocoMeses::getFromText)
								.orElseGet(() -> {
									if ("4010".equals(context.getCadoc())) {
										return BlocoMeses.JUN;
									}
									throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Invalid bloc");
								}))
				.companies(new ArrayList<String>(context.getCompanies()))
				.exercise(context.getExercise())
				.version(context.getVersion())
				.terminationCompanies(new ArrayList<String>(context.getTerminationCompanies()))
				.remittanceType(Optional.ofNullable(context.getRemittanceType()).orElse("I"))//
				.build();
		if (filterDTO.getCadoc().equals(ECadoc.C4016)) {
			if (BlocoMeses.DEZ.equals(filterDTO.getBloc())) {
				filterDTO.setInterval("12");
			}
			if (BlocoMeses.JUN.equals(filterDTO.getBloc())) {
				filterDTO.setInterval("06");
			}
		} else {
			filterDTO.setInterval(context.getInterval());
		}

		if (Integer.parseInt(filterDTO.getInterval()) <= 6) {
			filterDTO.setTipoDoc(Arrays.asList("F1"));
		} else {
			filterDTO.setTipoDoc(Arrays.asList("F2"));
		}

		filterDTO.setReleaseDate(YearMonth
				.of(Integer.valueOf(filterDTO.getExercise()), Integer.valueOf(filterDTO.getInterval())).atEndOfMonth());

		return filterDTO;
	}

	public static BalanceFilterDTO build(CdsReadEventContext context) {
		BalanceFilterDTO filterDTO = new BalanceFilterDTO();

		filterDTO.cadoc = (ECadoc) context.get("cadoc");
		filterDTO.companies = Arrays
				.asList((String[]) Optional.ofNullable(context.get("companies")).orElse(new String[0]));

		filterDTO.exercise = Optional.ofNullable(context.get("exercise")).orElse("").toString();
		filterDTO.version = Optional.ofNullable(context.get("version")).orElse("").toString();
		filterDTO.interval = Optional.ofNullable(context.get("interval")).orElse("").toString();
		filterDTO.tipoDoc = Arrays
				.asList((String[]) Optional.ofNullable(context.get("tipoDoc")).orElse(new String[0]));

		filterDTO.releaseDate = Optional.ofNullable((LocalDate) context.get("releaseDate"))
				.orElse(LocalDate.now());

		if (ECadoc.C4016.equals(filterDTO.cadoc)) {
			filterDTO.bloc = (BlocoMeses) context.get("blc");
		}

		filterDTO.terminationCompanies = Arrays
				.asList((String[]) Optional.ofNullable(context.get("termination")).orElse(new String[0]));

		return filterDTO;
	}

	public void setCadoc(String cadoc) {
		if (cadoc == null || cadoc.isBlank()) {
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Invalid CADOC");
		}

		this.cadoc = ECadoc.getCadoc(cadoc)
				.orElseThrow(() -> new ServiceException(ErrorStatuses.BAD_REQUEST, "Invalid CADOC"));
	}

	public void setBlc(String blc) {
		if (blc != null && !blc.isBlank()) {
			this.bloc = BlocoMeses.getFromText(blc).orElse(null);
		}
	}

	public void setTermination(String termination) {
		if (termination == null || termination.isBlank()) {
			this.terminationCompanies = new ArrayList<>();
			return;
		}

		this.terminationCompanies = Arrays.asList(termination.split(","));
	}

	public void generateReleaseDate() {
		this.setReleaseDate(
			YearMonth.of(Integer.valueOf(this.getExercise()), Integer.valueOf(this.getInterval())).atEndOfMonth()
		);
	}

	public void generateTpDoc() {
		if (Integer.parseInt(this.getInterval()) <= 6) {
			this.setTipoDoc(Arrays.asList("F1"));
		} else {
			this.setTipoDoc(Arrays.asList("F2"));
		}
	}

	public void generateInterval() {
		if (this.getCadoc().equals(ECadoc.C4016)) {
			if (BlocoMeses.DEZ.equals(this.getBloc())) {
				this.setInterval("12");
			}
			if (BlocoMeses.JUN.equals(this.getBloc())) {
				this.setInterval("06");
			}
		}
	}

	public int hashCodeKeyCache() {
		return Objects.hash(
				cadoc, companies, exercise, version, interval, tipoDoc, releaseDate, bloc);
	}

	public String toQuery() {
		String filterQuery = MessageFormat.format(
				"Exercicio eq ''{0}'' and " +
						"Versao eq ''{1}'' and Periodo eq ''{2}'' and " +
						"DtLanc eq datetime''{3}T00:00:00''",
				this.getExercise(), this.getVersion(), this.getInterval(), this.getReleaseDate());

		if (ECadoc.C4016.equals(this.getCadoc())) {
			filterQuery += " and " + this.getBloc().getText() + " eq 'X'";
		}
		// StringBuilder sb = new StringBuilder(filterQuery);
		return new StringBuilder(filterQuery)//
				.append(" and (")//
				.append(//
						companies.stream()//
								.map(e -> "Empresa eq '" + e + "'")
								.collect(Collectors.joining(" or "))//
				)//
				.append(") and ")//
				.append("(")
				.append(//
						tipoDoc.stream()//
								.map(t -> "TipoDoc eq '" + t + "'")//
								.collect(Collectors.joining(" or ")//
								)//
				)//
				.append(")")
				.toString();
		// sb.append("(");
		// sb.append(")");
	}
}
