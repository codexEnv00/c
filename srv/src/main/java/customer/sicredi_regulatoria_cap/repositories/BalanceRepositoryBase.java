package customer.sicredi_regulatoria_cap.repositories;

import com.sap.cds.CdsData;
import com.sap.cds.ql.StructuredType;
import com.sap.cds.services.persistence.PersistenceService;

import customer.sicredi_regulatoria_cap.dtos.balance.BalanceFilterDTO;

public abstract class BalanceRepositoryBase<T extends StructuredType<T>, U extends CdsData> extends RepositoryBase<T, U, BalanceFilterDTO> {
	
	protected BalanceRepositoryBase(PersistenceService db, Class<T> entity, Class<U> data) {
		super(db, entity, data);
	}
}
