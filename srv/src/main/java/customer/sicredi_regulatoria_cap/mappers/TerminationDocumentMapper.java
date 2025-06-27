package customer.sicredi_regulatoria_cap.mappers;

import java.util.stream.Stream;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import cds.gen.sicredi.db.entities.TerminationDocument;
import customer.sicredi_regulatoria_cap.dtos.export.TerminationDocumentExportDTO;

@Mapper
public interface TerminationDocumentMapper {

	TerminationDocumentMapper INSTANCE = Mappers.getMapper(TerminationDocumentMapper.class);

	@Mapping(target = "company", source = "company.name")
	@Mapping(target = "companyCode", source = "companyExternalCode")
	@Mapping(target = "document", source = "document")
	@Mapping(target = "exercise", source = "exercise")
	@Mapping(target = "interval", source = "interval")
	@Mapping(target = "item", source = "item")
	@Mapping(target = "amount", source = "amount")
	@Mapping(target = "razao", source = "razao")
	TerminationDocumentExportDTO map(TerminationDocument source);

	default Stream<TerminationDocumentExportDTO> mapAll(Stream<TerminationDocument> data) {
		return data.parallel().map(e -> map(e));
	}
}