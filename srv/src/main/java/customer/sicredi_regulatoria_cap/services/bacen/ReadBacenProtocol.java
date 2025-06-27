package customer.sicredi_regulatoria_cap.services.bacen;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.apache.http.HttpException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sap.cds.ql.CQL;
import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.Update;
import com.sap.cds.services.ErrorStatuses;
import com.sap.cds.services.ServiceException;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.sicredi.db.entities.ProtocolBacen_;
import cds.gen.sicredi.db.entities.Protocols;
import cds.gen.sicredi.db.entities.Protocols_;
import customer.sicredi_regulatoria_cap.mappers.ProtocolMapper;
import customer.sicredi_regulatoria_cap.services.APIDestinationService;
import customer.sicredi_regulatoria_cap.services.CompanyService;
import customer.sicredi_regulatoria_cap.services.PropertiesService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReadBacenProtocol {

	@Autowired
	private APIDestinationService apiDestinationService;

	@Autowired
	private PropertiesService propertiesService;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private CompanyService companyService;

	@Autowired
	private PersistenceService db;

	@Scheduled(fixedDelayString = "${bacen.protocol.search.status.delay.time:1800000}")
	public void processProtocolStatusScheduling() {
		log.info("Getting started Protocol Status Scheduling");
		var protocolsWithoutStatus = db.run(
			Select.from(Protocols_.class)
			.columns(p -> p.protocol())
			.where(
				p -> CQL.not(
					p.protocol().in(
						Select.from(ProtocolBacen_.class)
						.columns(pb -> pb.protocol_ID())
					)
				).and(p.protocol().ne("Error Requisição"))
			)
		).list();


		protocolsWithoutStatus.addAll(
			db.run(
				Select.from(ProtocolBacen_.class)
				.columns(p -> p.protocol_ID().as("protocol"))
				.where(
					p -> p.currentStatus().responsableUnity().isNull().and(
						CQL.not(
							p.currentStatus().code().in(35, 45, 55, 65, 75)
						)
					)
				)
			).list()
		);

		log.info("Protocols count {}", protocolsWithoutStatus.size());

		var errorsProtocols = new HashMap<String, String>();
		
		log.info("Protocols {}", protocolsWithoutStatus);

		protocolsWithoutStatus.parallelStream().forEach(
			p -> {
				try {
					loadProtocolBacen((String) p.get("protocol"));
				} catch (IOException | HttpException e) {
					errorsProtocols.put((String) p.get("protocol"), e.getMessage());
				}
			}
		);

		if (!errorsProtocols.isEmpty()) {
			log.error("Protocol errors {}", errorsProtocols.keySet());
			log.error("Error in protocols {}. Please check urge", errorsProtocols);
		}

		log.info("Processing protocol status finished");
	}

	public void loadProtocolBacen(String protocol) throws IOException, HttpException {
		mapper.findAndRegisterModules();
		mapper.disable(DeserializationFeature.UNWRAP_ROOT_VALUE);
		
		var protocolInfo = db.run(
			Select.from(Protocols_.class)
			.columns(c -> c.company().expand())
			.where(p -> p.protocol().eq(protocol))
		).first(Protocols.class);
		
		if (!protocolInfo.isPresent()) {
			return;
		}

		var auth = companyService.getCompanyAuthenticator(protocolInfo.get().getCompany());

		String authBase64 = Base64.getEncoder().encodeToString(
			(auth.get().getUser() + ":" + auth.get().getPassword()).getBytes()
		);

		var request = new Request(protocol);

		final StringEntity entity = new StringEntity(//
			mapper.writeValueAsString(request),
			ContentType.APPLICATION_JSON//
		);
		var response = apiDestinationService.post(//
			"/http/ConsultaProtocolo", //
			propertiesService.getCloudDestinationApi(), //
			entity, Response.class, //
			code -> code == 200, //
			mapper,
			new BasicHeader("Company-Authorization", authBase64)//
		);

		if (response.isEmpty()) {
			throw new ServiceException(ErrorStatuses.BAD_REQUEST, "Response is empty. Check if protocol " + protocol + " exists");
		}

		if (Objects.isNull(response.get().consultaEstadoAtual())) {
			return;
		}
		var protocolBacen = ProtocolMapper.INSTANCE.map(response.get());
		
		db.run(Delete.from(ProtocolBacen_.class).where(p -> p.protocol_ID().eq(protocolBacen.getProtocolId())));
		db.run(Insert.into(ProtocolBacen_.class).entry(protocolBacen));

		db.run(
			Update.entity(Protocols_.class)
			.set(p -> p.status(), p -> CQL.val(protocolBacen.getCurrentStatus().getDescription()))
			.where(p -> p.protocol().eq(protocolBacen.getProtocolId()))
		);
	}

	public static record Request (
		@JsonProperty("protocolo")
		String protocol
	) {}

	public static record Response (
		@JsonProperty("ConsultaEstadoAtual")
		ConsultaEstadoAtual consultaEstadoAtual,

		@JsonProperty("detalhamentoProcessamento")
		DetalhamentoProcessamento detalhamentoProcessamento
	) {}

	public static record ConsultaEstadoAtual(
		@JsonProperty("Resultado") @JsonDeserialize(using = ResultadoDeserializer.class) Resultado resultado
	) {}

	public static record Resultado(
		@JsonProperty("Arquivo") Arquivo arquivo
	) {}

	public static record Arquivo(
		@JsonProperty("Protocolo") String protocolo,
		@JsonProperty("TipoArquivo") String tipoArquivo,
		@JsonProperty("CodigoDocumento") String codigoDocumento,
		@JsonProperty("EstadoAtual") Estado estadoAtual,
		@JsonProperty("Emissor") Emissor emissor,
		@JsonProperty("Destinatario") Destinatario destinatario,
		@JsonProperty("Respostas") Respostas respostas,
		@JsonProperty("TamanhoArquivo") String tamanhoArquivo,
		@JsonProperty("NomeArquivoOrigem") String nomeArquivoOrigem,
		@JsonProperty("Hash") String hash,
		@JsonProperty("DataHoraTransmissao") String dataHoraTransmissao,
		@JsonProperty("Historico") Historico historico
	) {}

	public static record Emissor(
		@JsonProperty("Unidade") String unidade,
		@JsonProperty("Dependencia") String dependencia,
		@JsonProperty("Operador") String operador,
		@JsonProperty("NomeInstituicao") String nomeInstituicao
	) {}

	public static record Destinatario(
		@JsonProperty("Unidade") String unidade,
		@JsonProperty("NomeInstituicao") String nomeInstituicao
	) {}

	public static record Respostas(
		@JsonProperty("Protocolo") String protocolo
	) {}

	public static record Historico(
		@JsonProperty("Estado") List<Estado> estados
	) {}

	public static record Estado(
		@JsonProperty("DataHora") String dataHora,
		@JsonProperty("Codigo") String codigo,
		@JsonProperty("Descricao") String descricao,
		@JsonProperty("UnidadeResponsavel") String unidadeResponsavel,
		@JsonProperty("OperadorResponsavel") String operadorResponsavel
	) {}

	public static record DetalhamentoProcessamento(
		@JsonProperty("respostaCRD") RespostaCRD respostaCRD
	) {}

	public static record RespostaCRD(
		@JsonProperty("@sistemaNegocio") String sistemaNegocio,
		@JsonProperty("@documento") String documento,
		@JsonProperty("@protocolo") String protocolo,
		@JsonProperty("@dataBase") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy") LocalDate dataBase,
		@JsonProperty("@cnpj") String cnpj,
		@JsonProperty("situacao") Situacao situacao,
		@JsonProperty("erros") Erros erros
	) {}

	public static record Situacao(
		@JsonProperty("@codigo") String codigo,
		@JsonProperty("$") String descricao
	) {}

	public static record Erros(
		@JsonProperty("erro") @JsonDeserialize(using = ErrorDeserializer.class) List<Erro> erros
	) {}

	public static record Erro(
		@JsonProperty("@codigo") String codigo,
		@JsonProperty("descricao") String descricao,
		@JsonProperty("complemento") String complemento
	) {}

	private static class ResultadoDeserializer extends JsonDeserializer<Resultado> {
		public Resultado deserialize(JsonParser p, DeserializationContext ctxt) throws IOException ,JacksonException {
			ObjectMapper mapper = (ObjectMapper) p.getCodec();
			JsonNode node = mapper.readTree(p);

			if (node.isObject()) {
				return mapper.treeToValue(node, Resultado.class);
			}

			return null;
		};
	}
	private static class ErrorDeserializer extends JsonDeserializer<List<Erro>> {

		@Override
		public List<Erro> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
			ObjectMapper mapper = (ObjectMapper) p.getCodec();
			JsonNode node = mapper.readTree(p);
			List<Erro> errors = new ArrayList<>();

			if (node.isArray()) {
				for (JsonNode jsonNode : node) {
					errors.add(mapper.treeToValue(jsonNode, Erro.class));
				}
			} else {
				errors.add(mapper.treeToValue(node, Erro.class));
			}

			return errors;
		}

	}
}
