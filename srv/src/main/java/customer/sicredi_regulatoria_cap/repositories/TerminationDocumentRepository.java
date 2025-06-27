package customer.sicredi_regulatoria_cap.repositories;

import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnPredicate;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.sicredi.db.entities.TerminationDocument;
import cds.gen.sicredi.db.entities.TerminationDocument_;
import customer.sicredi_regulatoria_cap.dtos.balance.BalanceFilterDTO;

@Service
public class TerminationDocumentRepository extends BalanceRepositoryBase<TerminationDocument_, TerminationDocument> {

	public TerminationDocumentRepository(PersistenceService db) {
		super(db, TerminationDocument_.class, TerminationDocument.class);
	}

	@Override
	public Select<TerminationDocument_> buildSelect() {
		return Select.from(TerminationDocument_.class)
				.columns(
						t -> t._all(),
						t -> t.company().expand(
								c -> c._all()//
						)//
				);
	}

	@Override
	public Function<TerminationDocument_, CqnPredicate> getWhere(BalanceFilterDTO filterDTO) {
		filterDTO.generateInterval();
		filterDTO.generateReleaseDate();
		filterDTO.generateTpDoc();

		return t -> t.company().externalCode().in(filterDTO.getCompanies()).and(//
				t.exercise().eq(filterDTO.getExercise()).and(//
						t.interval().eq(filterDTO.getInterval()).and(//
								t.cadoc().eq(filterDTO.getCadoc().getName()).and(//
										t.dtLanc().eq(filterDTO.getReleaseDate()).and(//
												t.tpDoc().in(filterDTO.getTipoDoc())//
										)//
								)//
						)//
				)//
		);
	}

}
