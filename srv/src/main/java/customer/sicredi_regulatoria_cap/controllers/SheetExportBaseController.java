package customer.sicredi_regulatoria_cap.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;

import com.opencsv.exceptions.CsvException;

import customer.sicredi_regulatoria_cap.services.sheet.ExportSheetBaseService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

public abstract class SheetExportBaseController<T> {

	public abstract ExportSheetBaseService<?, T> getExportSheetService();

	protected DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy_HHmm");

	public String getBaseName() {
		return "export";
	}

	protected String getCompleteFileName() {
		return getBaseName() + "_" + LocalDateTime.now().format(formatter);
	}

	@GetMapping("/export/csv")
	public void exportCSV(HttpServletResponse response, @Valid T filterDTO) throws IOException, CsvException {

		response.setContentType("text/csv");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + getCompleteFileName() + ".csv\"");

		response.setStatus(HttpStatus.OK.value());

		getExportSheetService().exportReportCSV(filterDTO, response.getOutputStream());
	}

	@GetMapping("/export/xlsx")
	public void exportXlsx(HttpServletResponse response, @Valid T filterDTO) throws IOException {
		
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + getCompleteFileName() + ".xlsx\"");
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setStatus(HttpStatus.OK.value());

		getExportSheetService().exportReportXLSX(filterDTO, response.getOutputStream());

		// response.flushBuffer();
	}
}
