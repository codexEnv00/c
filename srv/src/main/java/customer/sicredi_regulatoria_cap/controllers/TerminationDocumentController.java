package customer.sicredi_regulatoria_cap.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import customer.sicredi_regulatoria_cap.dtos.balance.BalanceFilterDTO;
import customer.sicredi_regulatoria_cap.services.sheet.ExportSheetBaseService;
import customer.sicredi_regulatoria_cap.services.sheet.TerminationDocumentService;

@RestController
@RequestMapping("/balance/termination")
public class TerminationDocumentController extends SheetExportBaseController<BalanceFilterDTO> {

    @Autowired
    private TerminationDocumentService terminationDocumentService;

	@Override
	public ExportSheetBaseService<?, BalanceFilterDTO> getExportSheetService() {
        return terminationDocumentService;
	}

    @Override
	public String getBaseName() {
		return "documentos_contabeis";
	}
}
