package customer.sicredi_regulatoria_cap.mappers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Objects;
import java.util.stream.Stream;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import cds.gen.sicredi.db.entities.ProtocolDetails;
import customer.sicredi_regulatoria_cap.dtos.ReportDTO;

@Mapper
public interface ReportMapper {

    ReportMapper INSTANCE = Mappers.getMapper(ReportMapper.class);

    @Mapping(target = "company", source = "company.name")
    @Mapping(target = "cnpj", source = "company.cnpj")
    @Mapping(target = "cosif", source = "cosifAccount")
    @Mapping(target = "razao", source = "razaoAccount")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "cadocBalance", source = "cadocBalance")
    @Mapping(target = "protocol", source = "protocol.protocol")
    @Mapping(target = "createdBy", source = "protocol.createdBy")
    @Mapping(target = "createdAt", source = "protocol.createdAt", qualifiedByName = "instantToLocalDateTime")
    @Mapping(target = "status", source = "protocol.status")
    @Mapping(target = "cadoc", source = "protocol.cadoc")
    ReportDTO map(ProtocolDetails details);
    
    @Named("instantToLocalDateTime")
    default LocalDateTime instantToLocalDateTime(Instant instant) {
        if (Objects.isNull(instant)) {
            return null;
        }
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    default Stream<ReportDTO> mapAll(Stream<ProtocolDetails> protocols) {
        return protocols.parallel()
            .map(e -> map(e));
    }
}