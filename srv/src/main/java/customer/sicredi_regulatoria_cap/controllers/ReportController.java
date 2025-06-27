package customer.sicredi_regulatoria_cap.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.opencsv.exceptions.CsvException;

import customer.sicredi_regulatoria_cap.dtos.ReportFilterDTO;
import customer.sicredi_regulatoria_cap.handlers.ReportServiceHandler;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/report")
public class ReportController {

	@Autowired
	private ReportServiceHandler reportServiceHandler;

	@GetMapping("/export/csv")
	public void exportCSV(HttpServletResponse response, @Valid ReportFilterDTO filterDTO) throws IOException, CsvException {
		reportServiceHandler.exportReportCSV(filterDTO, response.getOutputStream());

		response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report.csv\"");
		response.setContentType("text/csv");

		response.flushBuffer();
	}

	@GetMapping("/export/xlsx")
	public void exportXlsx(HttpServletResponse response, @Valid ReportFilterDTO filterDTO) throws IOException {
		reportServiceHandler.exportReportXLSX(filterDTO, response.getOutputStream());

		response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report.xlsx\"");
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

		response.flushBuffer();
	}
}
