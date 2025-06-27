package customer.sicredi_regulatoria_cap.repositories;

import java.util.List;
import java.util.function.Function;

import com.sap.cds.CdsData;
import com.sap.cds.Result;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.StructuredType;
import com.sap.cds.ql.cqn.CqnPredicate;
import com.sap.cds.services.persistence.PersistenceService;

public abstract class RepositoryBase <T extends StructuredType<T>, U extends CdsData, S> {

	protected PersistenceService db;

	protected Class<T> entity;
	protected Class<U> data;

	protected RepositoryBase(PersistenceService db, Class<T> entity, Class<U> data) {
		this.db = db;
		this.entity = entity;
		this.data = data;
	}

	public long count(S filterDTO) {
		return db.run(buildSelectWithWhere(filterDTO).inlineCount()).inlineCount();
	}

	public List<U> findAllByFilter(S filterDTO) {
		return resultAllByFilter(filterDTO).listOf(data);
	}

	public Result resultAllByFilter(S filterDTO) {
		return db.run(buildSelectWithWhere(filterDTO));
	}

	public Result findAllByFilterWithCount(S filterDTO) {
		return db.run(buildSelectWithWhere(filterDTO).inlineCount(), 1000, 0);
	}

	public Result findAllByFilterWithCount(S filterDTO, long top, long skip) {
		return db.run(buildSelectWithWhereAndCount(filterDTO, top, skip));
	}

	public Result findAllBySelect(Select<T> select) {
		return db.run(select);
	}

	public Select<T> buildSelectWithWhereAndCount(S filterDTO, long top, long skip) {
		return buildSelectWithWhere(filterDTO).limit(top, skip).inlineCount();
	}

	public Select<T> buildSelectWithWhere(S filterDTO) {
		return buildSelect().where(getWhere(filterDTO));
	}

	public abstract Select<T> buildSelect(); 

	public abstract Function<T, CqnPredicate> getWhere(S filterDTO);
}
