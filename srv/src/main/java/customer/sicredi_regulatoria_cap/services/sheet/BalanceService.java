package customer.sicredi_regulatoria_cap.services.sheet;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import org.apache.commons.lang3.function.TriConsumer;
import org.dhatim.fastexcel.Worksheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import customer.sicredi_regulatoria_cap.dtos.balance.BalanceFilterDTO;
import customer.sicredi_regulatoria_cap.dtos.export.BalanceExportDTO;
import customer.sicredi_regulatoria_cap.mappers.BalanceMapper;
import customer.sicredi_regulatoria_cap.services.SheetService;
import customer.sicredi_regulatoria_cap.services.bacen.ReadBacenService;
import customer.sicredi_regulatoria_cap.services.bacen.ReadBacenService.CompanyAccount;

@Service
public class BalanceService extends ExportSheetBaseService<BalanceExportDTO, BalanceFilterDTO> {

	public BalanceService(SheetService sheetService) {
		super(sheetService);
	}

	@Autowired
	private ReadBacenService readBacenService;

	@Override
	protected Stream<BalanceExportDTO> generateData(BalanceFilterDTO filter) {

		Map<ReadBacenService.CompanyAccount, BigDecimal> mapTerminationDocuments = readBacenService
				.getTerminationDocuments(filter);

		AtomicReference<BalanceExportDTO> previousRef = new AtomicReference<>();

		List<BalanceExportDTO> balances = new ArrayList<>();
		readBacenService.getBalanceStream(filter)
				.map(current -> {
					BalanceExportDTO export = BalanceMapper.INSTANCE.map(current);
					return export;
				})
				.forEach(balance -> {
					processBalanceTermination(mapTerminationDocuments, balance, previousRef);
					balances.add(balance);
				});

		return balances.stream();

	}

	@Override
	protected Class<BalanceExportDTO> getExportClass() {
		return BalanceExportDTO.class;
	}

	@Override
	protected TriConsumer<Worksheet, BalanceExportDTO, Integer> getRowConsumer() {
		return (ws, balance, i) -> {
			ws.value(i, 0, balance.getCompany());
			ws.value(i, 1, balance.getCnpj());
			ws.value(i, 2, balance.getCosif());
			ws.value(i, 3, balance.getDescription());
			ws.value(i, 4, balance.getRazao());
			ws.value(i, 5, balance.getCadocBalance());
			ws.value(i, 6, balance.getOriginalCadocBalance());
		};
	}

	private void processBalanceTermination(Map<CompanyAccount, BigDecimal> mapTerminationDocuments,
			BalanceExportDTO current, AtomicReference<BalanceExportDTO> previousRef) {
		BalanceExportDTO previous = previousRef.get();

		if (previous != null) {
			if (current.getHierarchyLevel() > previous.getHierarchyLevel()) {
				current.setParent(previous);
			} else if (current.getHierarchyLevel() == previous.getHierarchyLevel()) {
				current.setParent(previous.getParent());
			} else {
				BalanceExportDTO parent = previous.getParent();
				while (parent != null && parent.getHierarchyLevel() >= current.getHierarchyLevel()) {
					parent = parent.getParent();
				}
				current.setParent(parent);
			}

			CompanyAccount key = new CompanyAccount(current.getCompany(), current.getRazao());
			if (mapTerminationDocuments.containsKey(key)) {
				final BigDecimal sumValue = Optional.ofNullable(mapTerminationDocuments.get(key)).orElse(BigDecimal.ZERO);
				if (sumValue.signum() == 0) {
					return;
				}

				BalanceExportDTO actual = current;
				while (actual != null) {
					actual.setCadocBalance(
							Optional.ofNullable(actual.getCadocBalance()).orElse(BigDecimal.ZERO)
							.add(sumValue)//
					);
					actual = actual.getParent();
				}

			}
		}
		previousRef.set(current);
	}
}
