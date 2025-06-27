package customer.sicredi_regulatoria_cap.dtos.balance;

import java.math.BigDecimal;
import java.util.Optional;

import cds.gen.balanceservice.Balance;

public interface BalanceResponseDTO extends Balance {

	default void setParent(BalanceResponseDTO balance) {
		put("parent", balance);
	}

	@Override
	default void setCadocBalance(BigDecimal cadocBalance) {
		put("cadocBalance", cadocBalance);
	}

	default BalanceResponseDTO getParent() {
		return (BalanceResponseDTO) get("parent");
	}

	default String getNodeID() {
		return Optional.ofNullable(get("nodeID")).map(Object::toString).orElse(null);
	}

	default Integer getHierarchyLevel() {
		return Optional.ofNullable(get("hierarchyLevel")).map(Object::toString).map(Integer::parseInt).orElse(null);
	}

	default String getParentNodeID() {
		return Optional.ofNullable(get("parentNodeID")).map(Object::toString).orElse(null);
	}

	default String getDrillState() {
		return Optional.ofNullable(get("drillState")).map(Object::toString).orElse(null);
	}

	default String getCompany() {
		return Optional.ofNullable(get("company")).map(Object::toString).orElse(null);
	}

	default String getCnpj() {
		return Optional.ofNullable(get("cnpj")).map(Object::toString).orElse(null);
	}

	default String getCosif() {
		return Optional.ofNullable(get("cosif")).map(Object::toString).orElse(null);
	}

	default String getRazao() {
		return Optional.ofNullable(get("razao")).map(Object::toString).orElse(null);
	}

	default String getDescription() {
		return Optional.ofNullable(get("description")).map(Object::toString).orElse(null);
	}

	default BigDecimal getCadocBalance() {
		return Optional.ofNullable(get("cadocBalance")).map(e -> (BigDecimal)e).orElse(null);
	}

	default BigDecimal getOriginalCadocBalance() {
		return Optional.ofNullable(get("originalCadocBalance")).map(e -> (BigDecimal)e).orElse(null);
	}

}
