package customer.sicredi_regulatoria_cap.mappers;

import java.util.stream.Stream;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import cds.gen.sicredi.db.entities.OutsideBalance;
import customer.sicredi_regulatoria_cap.dtos.export.OutsideBalanceExportDTO;

@Mapper
public interface OutsideBalanceMapper {

    OutsideBalanceMapper INSTANCE = Mappers.getMapper(OutsideBalanceMapper.class);

    @Mapping(target = "company", source = "company.name")
    @Mapping(target = "cnpj", source = "company.cnpj")
    @Mapping(target = "razao", source = "razaoAccount")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "cadocBalance", source = "cadocBalance")
    OutsideBalanceExportDTO map(OutsideBalance source);

    default Stream<OutsideBalanceExportDTO> mapAll(Stream<OutsideBalance> data) {
        return data.parallel().map(e -> map(e));
    }
}
