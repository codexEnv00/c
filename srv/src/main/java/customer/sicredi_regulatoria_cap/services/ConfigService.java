package customer.sicredi_regulatoria_cap.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.http.HttpException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import cds.gen.configservice.TipoDoc;
import cds.gen.configservice.Versao;
import customer.sicredi_regulatoria_cap.dtos.WrapperResultDTO;
import customer.sicredi_regulatoria_cap.dtos.config.TipoDocDTO;
import customer.sicredi_regulatoria_cap.dtos.config.VersaoDTO;
import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class ConfigService {

	private static final String TIPO_DOC_PATH = "/http/cadoc4010/getTipoDocSet?$format=json";
	private static final String VERSAO_PATH = "/http/cadoc4010/getVersaoSet?$format=json";

	@Autowired
	private APIDestinationService api;

	@Autowired
	private PropertiesService propertiesService;

	public List<TipoDoc> getTipoDocs() throws HttpException, IOException {
		Optional<WrapperResultDTO<TipoDocDTO>> result = api.get(TIPO_DOC_PATH, propertiesService.getCloudDestinationApi(), new TypeReference<WrapperResultDTO<TipoDocDTO>>() {}, s -> s == 200);
		
		if (result.isEmpty()) {
			log.warn("TipoDoc response is empty");
			return new ArrayList<>();
		}

		return result.get().getResults()
			.parallelStream()
			.map(e -> {
				TipoDoc tp = TipoDoc.create(e.getTipoDoc());
				tp.setDescrTipoDoc(e.getDesc());

				return tp;
			})
			.toList();
	}

	public List<Versao> getVersoes() throws HttpException, IOException {
		return getGeneric(VERSAO_PATH, new TypeReference<WrapperResultDTO<VersaoDTO>>() {}, (VersaoDTO e) -> {
			Versao version = Versao.create(e.getVersn());
			version.setDescrVersao(e.getVstxt());
			return version;
		});
	}

	private <T, U> List<T> getGeneric(String path, TypeReference<WrapperResultDTO<U>> type, Function<U, T> mapper) throws HttpException, IOException {
		Optional<WrapperResultDTO<U>> result = api.get(path, propertiesService.getCloudDestinationApi(), type, s -> s == 200);
		
		if (result.isEmpty()) {
			log.warn("Response is empty");
			return new ArrayList<>();
		}

		return result.get().getResults()//
			.parallelStream()//
			.map(mapper)
			.toList();
	}
}
