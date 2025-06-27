package customer.sicredi_regulatoria_cap.services.sheet;

import java.util.stream.Stream;

import org.apache.commons.lang3.function.TriConsumer;
import org.dhatim.fastexcel.Worksheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cds.gen.sicredi.db.entities.TerminationDocument;
import customer.sicredi_regulatoria_cap.dtos.balance.BalanceFilterDTO;
import customer.sicredi_regulatoria_cap.dtos.export.TerminationDocumentExportDTO;
import customer.sicredi_regulatoria_cap.mappers.TerminationDocumentMapper;
import customer.sicredi_regulatoria_cap.repositories.TerminationDocumentRepository;
import customer.sicredi_regulatoria_cap.services.SheetService;

@Service
public class TerminationDocumentService extends ExportSheetBaseService<TerminationDocumentExportDTO, BalanceFilterDTO> {

	@Autowired
	private TerminationDocumentRepository repository;

	public TerminationDocumentService(SheetService sheetService) {
		super(sheetService);
	}

	@Override
	protected Stream<TerminationDocumentExportDTO> generateData(BalanceFilterDTO filter) {
		return repository.resultAllByFilter(filter)
				// .parallelStream()
				.streamOf(TerminationDocument.class)
				.map(TerminationDocumentMapper.INSTANCE::map);
	}

	@Override
	protected Class<TerminationDocumentExportDTO> getExportClass() {
		return TerminationDocumentExportDTO.class;
	}

	@Override
	protected TriConsumer<Worksheet, TerminationDocumentExportDTO, Integer> getRowConsumer() {
		return (ws, data, i) -> {
			ws.value(i, 0, data.getCompanyCode());
			ws.value(i, 1, data.getCompany());
			ws.value(i, 2, data.getDocument());
			ws.value(i, 3, data.getExercise());
			ws.value(i, 4, data.getInterval());
			ws.value(i, 5, data.getItem());
			ws.value(i, 6, data.getAmount());
			ws.value(i, 7, data.getRazao());
		};
	}

}
