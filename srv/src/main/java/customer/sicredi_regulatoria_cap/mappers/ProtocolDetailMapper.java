package customer.sicredi_regulatoria_cap.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import cds.gen.balanceservice.Balance;
import cds.gen.sicredi.db.entities.ProtocolDetails;

@Mapper
public interface ProtocolDetailMapper {

	ProtocolDetailMapper INSTANCE = Mappers.getMapper(ProtocolDetailMapper.class);

	@Mapping(target = "cosifAccount", source = "cosifAccount")
	@Mapping(target = "level", source = "level")
	@Mapping(target = "parentKey", source = "parentKey")
	@Mapping(target = "drillState", source = "drillState")
	@Mapping(target = "nodeID", source = "nodeID")
	@Mapping(target = "razaoAccount", source = "razaoAccount")
	@Mapping(target = "cadocBalance", source = "cadocBalance")
	@Mapping(target = "description", source = "description")
	@Mapping(target = "companyExternalCode", source = "company")
	ProtocolDetails map(Balance source);

	default ProtocolDetails createProtocolDetails() {
		return ProtocolDetails.create();
	}
}
