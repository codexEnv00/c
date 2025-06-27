package customer.sicredi_regulatoria_cap.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import customer.sicredi_regulatoria_cap.dtos.balance.BalanceFilterDTO;
import customer.sicredi_regulatoria_cap.services.sheet.BalanceService;
import customer.sicredi_regulatoria_cap.services.sheet.ExportSheetBaseService;

@RestController
@RequestMapping("/balance")
public class BalanceController extends SheetExportBaseController<BalanceFilterDTO> {

	@Autowired
	private BalanceService balanceService;

	@Override
	public ExportSheetBaseService<?, BalanceFilterDTO> getExportSheetService() {
		return balanceService;
	}

	@Override
	public String getBaseName() {
		return "balanco";
	}

}
