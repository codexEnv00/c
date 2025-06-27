package customer.sicredi_regulatoria_cap.mappers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import cds.gen.reportservice.ProtocolBacenError;
import cds.gen.sicredi.db.entities.ProtocolBacen;
import cds.gen.sicredi.db.entities.ProtocolBacenStatus;
import cds.gen.sicredi.db.entities.Protocols;
import customer.sicredi_regulatoria_cap.dtos.balance.BalanceFilterDTO;
import customer.sicredi_regulatoria_cap.services.bacen.ReadBacenProtocol;
import customer.sicredi_regulatoria_cap.services.cadoc4500.Cadoc4500Service;

@Mapper
public interface ProtocolMapper {

	ProtocolMapper INSTANCE = Mappers.getMapper(ProtocolMapper.class);

	@Mapping(target = "exercise", source = "exercise")
	@Mapping(target = "interval", source = "interval")
	Protocols map(Cadoc4500Service.Cadoc4500FilterDTO source);

	@Mapping(target = "cadoc", source = "cadoc.name")
	@Mapping(target = "exercise", source = "exercise")
    @Mapping(target = "interval", source = "interval")
    @Mapping(target = "version", source = "version")
    @Mapping(target = "tipoDoc", expression = "java(source.getTipoDoc().get(0))")
    @Mapping(target = "releaseDate", source = "releaseDate")
    @Mapping(target = "bloc", source = "bloc.text")
    Protocols map(BalanceFilterDTO source);


	@Mapping(target = "protocolId", source = "consultaEstadoAtual.resultado.arquivo.protocolo")
	@Mapping(target = "fileType", source = "consultaEstadoAtual.resultado.arquivo.tipoArquivo")
	@Mapping(target = "cadoc", source = "consultaEstadoAtual.resultado.arquivo.codigoDocumento")
	@Mapping(target = "currentStatus", source = "consultaEstadoAtual.resultado.arquivo.estadoAtual")

	@Mapping(target = "issuerInstitutionUnity", source = "consultaEstadoAtual.resultado.arquivo.emissor.unidade")
	@Mapping(target = "issuerInstitutionName", source = "consultaEstadoAtual.resultado.arquivo.emissor.nomeInstituicao")
	@Mapping(target = "issuerDependency", source = "consultaEstadoAtual.resultado.arquivo.emissor.dependencia")
	@Mapping(target = "issuerOperator", source = "consultaEstadoAtual.resultado.arquivo.emissor.operador")

	@Mapping(target = "recipientUnity", source = "consultaEstadoAtual.resultado.arquivo.destinatario.unidade")
	@Mapping(target = "recipientName", source = "consultaEstadoAtual.resultado.arquivo.destinatario.nomeInstituicao")
	
	@Mapping(target = "fileSize", source = "consultaEstadoAtual.resultado.arquivo.tamanhoArquivo")
	@Mapping(target = "fileNameOrigin", source = "consultaEstadoAtual.resultado.arquivo.nomeArquivoOrigem")
	@Mapping(target = "hash", source = "consultaEstadoAtual.resultado.arquivo.hash")
	@Mapping(target = "transmitionDateTime", source = "consultaEstadoAtual.resultado.arquivo.dataHoraTransmissao", qualifiedByName = "toInstantDate")
	
	@Mapping(target = "historical", source = "consultaEstadoAtual.resultado.arquivo.historico", qualifiedByName = "toHistorical")
	
	@Mapping(target = "system", source = "detalhamentoProcessamento.respostaCRD.sistemaNegocio")
	@Mapping(target = "baseDate", source = "detalhamentoProcessamento.respostaCRD.dataBase")
	@Mapping(target = "cnpj", source = "detalhamentoProcessamento.respostaCRD.cnpj")
	@Mapping(target = "situationCode", source = "detalhamentoProcessamento.respostaCRD.situacao.codigo")
	@Mapping(target = "situationDescription", source = "detalhamentoProcessamento.respostaCRD.situacao.descricao")

	@Mapping(target = "error", source = "detalhamentoProcessamento.respostaCRD.erros", qualifiedByName = "toError")
	ProtocolBacen map(ReadBacenProtocol.Response source);

	@Mapping(target = "dateHour", source = "dataHora", qualifiedByName = "toInstantDate")
	@Mapping(target = "code", source = "codigo")
	@Mapping(target = "description", source = "descricao")
	@Mapping(target = "responsableUnity", source = "unidadeResponsavel")
	@Mapping(target = "responsableOperator", source = "operadorResponsavel")
	ProtocolBacenStatus map(ReadBacenProtocol.Estado source);

	@Mapping(target = "code", source = "codigo")
	@Mapping(target = "description", source = "descricao")
	@Mapping(target = "complement", source = "complemento")
	ProtocolBacenError map(ReadBacenProtocol.Erro source);

	@Named("toHistorical")
	default List<ProtocolBacenStatus> toHistorical(ReadBacenProtocol.Historico historico) {
		return historico.estados().stream().map(e -> map(e)).toList();
	}

	@Named("toError")
	default List<ProtocolBacenError> toError(ReadBacenProtocol.Erros errors) {
		if (errors != null && errors.erros() != null) {
			return errors.erros().stream().map(e -> map(e)).toList();
		}

		return Arrays.asList();

	}

	@Named("toInstantDate")
	default Instant toInstantDate(String date) {
		return LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME).atZone(ZoneId.of("America/Sao_Paulo")).toInstant();
	}

	default Protocols createProtocols() {
		return Protocols.create();
	}

	default ProtocolBacen createProtocolBacen() {
		return ProtocolBacen.create();
	}

	default ProtocolBacenStatus createProtocolBacenStatus() {
		return ProtocolBacenStatus.create();
	}

	default ProtocolBacenError createProtocolBacenError() {
		return ProtocolBacenError.create();
	}
} 
