package customer.sicredi_regulatoria_cap.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import cds.gen.sicredi.db.entities.Companies;
import customer.sicredi_regulatoria_cap.dtos.company.CompanyDTO;

@Mapper
public interface CompanyMapper {

	CompanyMapper INSTANCE = Mappers.getMapper(CompanyMapper.class);

	@Mapping(target = "externalCode", source = "externalCode")
	@Mapping(target = "name", source = "description")
	@Mapping(target = "cnpj", source = "cnpj")
	Companies toMap(CompanyDTO source);

	default Companies createCompany() {
		return Companies.create();
	}
}