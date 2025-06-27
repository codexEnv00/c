package customer.sicredi_regulatoria_cap.services.sheet;

import java.util.stream.Stream;

import org.apache.commons.lang3.function.TriConsumer;
import org.dhatim.fastexcel.Worksheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cds.gen.sicredi.db.entities.OutsideBalance;
import customer.sicredi_regulatoria_cap.dtos.balance.BalanceFilterDTO;
import customer.sicredi_regulatoria_cap.dtos.export.OutsideBalanceExportDTO;
import customer.sicredi_regulatoria_cap.mappers.OutsideBalanceMapper;
import customer.sicredi_regulatoria_cap.repositories.OutsideBalanceRepository;
import customer.sicredi_regulatoria_cap.services.SheetService;

@Service
public class OutsideBalanceService extends ExportSheetBaseService<OutsideBalanceExportDTO, BalanceFilterDTO> {

	@Autowired
	private OutsideBalanceRepository repository;

	public OutsideBalanceService(SheetService sheetService) {
		super(sheetService);
	}

	@Override
	protected Stream<OutsideBalanceExportDTO> generateData(BalanceFilterDTO filter) {
		return repository.resultAllByFilter(filter)
				// .parallelStream()
				.streamOf(OutsideBalance.class)
				.map(OutsideBalanceMapper.INSTANCE::map);
	}

	@Override
	protected Class<OutsideBalanceExportDTO> getExportClass() {
		return OutsideBalanceExportDTO.class;
	}

	@Override
	protected TriConsumer<Worksheet, OutsideBalanceExportDTO, Integer> getRowConsumer() {
		return (ws, data, i) -> {
			ws.value(i, 0, data.getCompany());
			ws.value(i, 1, data.getCnpj());
			ws.value(i, 2, data.getRazao());
			ws.value(i, 3, data.getDescription());
			ws.value(i, 4, data.getCadocBalance());
		};
	}

}
