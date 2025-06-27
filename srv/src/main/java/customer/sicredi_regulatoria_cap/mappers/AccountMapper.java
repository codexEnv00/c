package customer.sicredi_regulatoria_cap.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
@Mapper
public interface AccountMapper {

	AccountMapper INSTANCE = Mappers.getMapper( AccountMapper.class);

	public static record AccountValue(String ecc, String bacen) {
	}

	public static record CompanySegment(String company, String segment) {
	}
}