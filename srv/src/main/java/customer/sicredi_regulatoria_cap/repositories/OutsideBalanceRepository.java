package customer.sicredi_regulatoria_cap.repositories;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnPredicate;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.sicredi.db.entities.OutsideBalance;
import cds.gen.sicredi.db.entities.OutsideBalance_;
import customer.sicredi_regulatoria_cap.dtos.balance.BalanceFilterDTO;

@Service
public class OutsideBalanceRepository extends BalanceRepositoryBase<OutsideBalance_, OutsideBalance> {

	public OutsideBalanceRepository(PersistenceService db) {
		super(db, OutsideBalance_.class, OutsideBalance.class);
	}

	@Override
	public Select<OutsideBalance_> buildSelect() {
		return Select.from(OutsideBalance_.class)//
				.columns(//
						o -> o._all(), //
						o -> o.company().expand(//
								c -> c._all()//
						)//
				);//
	}

	@Override
	public Function<OutsideBalance_, CqnPredicate> getWhere(BalanceFilterDTO filterDTO) {
		filterDTO.generateInterval();
		filterDTO.generateReleaseDate();
		filterDTO.generateTpDoc();

		return o -> o.company().externalCode().in(filterDTO.getCompanies()).and(//
				o.version().eq(filterDTO.getVersion()).and(//
						o.interval().eq(filterDTO.getInterval()).and(//
								o.exercise().eq(filterDTO.getExercise()).and(//
										o.cadoc().eq(filterDTO.getCadoc().getName())//
								)//
						)//
				)//
		);
	}

}
