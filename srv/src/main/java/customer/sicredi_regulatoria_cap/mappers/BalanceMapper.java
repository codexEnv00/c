package customer.sicredi_regulatoria_cap.mappers;

import java.util.Optional;
import java.util.stream.Stream;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import cds.gen.balanceservice.Balance;
import cds.gen.sicredi.db.entities.Balances;
import cds.gen.sicredi.db.entities.Companies;
import customer.sicredi_regulatoria_cap.dtos.balance.BalanceResponseDTO;
import customer.sicredi_regulatoria_cap.dtos.export.BalanceExportDTO;

@Mapper
public interface BalanceMapper {

	BalanceMapper INSTANCE = Mappers.getMapper(BalanceMapper.class);

	default BalanceResponseDTO toBalanceHierarchical(Balances balances) {
		Balance b = Balance.create();
		b.setNodeID(balances.getNodeID());

		b.setHierarchyLevel(Optional.ofNullable(balances.getLevel()).orElse(1) - 1);
		
		b.setParentNodeID(balances.getParentKey());

		b.setCompany("");
		Optional<Companies> company = Optional.ofNullable(balances.getCompany());
		if (company.isPresent()) {
			b.setCompany(company.get().getName());
		}
		
		b.setCosif(balances.getCosifAccount());
		b.setRazao(balances.getRazaoAccount());
		b.setDescription(balances.getDescription());
		b.setCadocBalance(balances.getCadocBalance());
		b.setDrillState(balances.getDrillState());

		return (BalanceResponseDTO) b;
	}

	@Mapping(target = "company", source = "company")
	@Mapping(target = "cnpj", source = "cnpj")
	@Mapping(target = "cosif", source = "cosif")
	@Mapping(target = "razao", source = "razao")
	@Mapping(target = "description", source = ".", qualifiedByName = "formatDescription")
	@Mapping(target = "cadocBalance", source = "cadocBalance")
	@Mapping(target = "originalCadocBalance", source = "originalCadocBalance")
	@Mapping(target = "hierarchyLevel", source = "hierarchyLevel")
	BalanceExportDTO map (BalanceResponseDTO balance);

	@Named("formatDescription")
	default String formatDescription(BalanceResponseDTO balance) {
		return "    ".repeat(balance.getHierarchyLevel()) + balance.getDescription();
	}

    default Stream<BalanceExportDTO> mapAll(Stream<BalanceResponseDTO> data) {
		return data.parallel().map(e -> map(e));
	}
}
