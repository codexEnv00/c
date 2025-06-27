package customer.sicredi_regulatoria_cap.repositories;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnPredicate;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.sicredi.db.entities.cadoc4500.Estban;
import cds.gen.sicredi.db.entities.cadoc4500.Estban_;
import customer.sicredi_regulatoria_cap.services.cadoc4500.Cadoc4500Service.Cadoc4500FilterDTO;

@Service
public class EstbanCadoc4500Repository extends RepositoryBase<Estban_, Estban, Cadoc4500FilterDTO> {

	public EstbanCadoc4500Repository(PersistenceService db) {
		super(db, Estban_.class, Estban.class);
	}

	@Override
	public Select<Estban_> buildSelect() {
		return Select.from(Estban_.class)//
				.columns(//
						e -> e._all(), //
						e -> e.company().expand(//
								c -> c._all()//
						)//
				);
	}

	@Override
	public Function<Estban_, CqnPredicate> getWhere(Cadoc4500FilterDTO filterDTO) {
		return e -> e.company_externalCode().in(filterDTO.companies())//
				.and(e.exercise().eq(filterDTO.exercise()))//
				.and(e.interval().eq(filterDTO.interval()));
	}

}
