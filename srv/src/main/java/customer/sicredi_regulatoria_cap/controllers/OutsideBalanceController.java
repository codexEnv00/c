package customer.sicredi_regulatoria_cap.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import customer.sicredi_regulatoria_cap.dtos.balance.BalanceFilterDTO;
import customer.sicredi_regulatoria_cap.services.sheet.ExportSheetBaseService;
import customer.sicredi_regulatoria_cap.services.sheet.OutsideBalanceService;

@RestController
@RequestMapping("/balance/outside")
public class OutsideBalanceController extends SheetExportBaseController<BalanceFilterDTO> {

	@Autowired
	private OutsideBalanceService outsideBalanceService;

	@Override
	public ExportSheetBaseService<?, BalanceFilterDTO> getExportSheetService() {
		return outsideBalanceService;
	}

	@Override
	public String getBaseName() {
		return "fora_balanco";
	}
}
