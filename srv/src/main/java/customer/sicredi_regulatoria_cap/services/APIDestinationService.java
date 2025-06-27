package customer.sicredi_regulatoria_cap.services;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sap.cloud.sdk.cloudplatform.connectivity.DefaultHttpClientFactory;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpClientFactory;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class APIDestinationService {

	private final HttpClientFactory HTTP_CLIENT_FACTORY = DefaultHttpClientFactory.builder()
			.timeoutMilliseconds(120 * 60 * 1000)
			.maxConnectionsPerRoute(100)
			.maxConnectionsTotal(200)
			.build();

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private DestinationService destinationService;

	public <T> Optional<T> get(String path, HttpDestination destination, TypeReference<T> type)
			throws IOException, HttpException {
		return get(path, destination, type, (code) -> code >= 200 && code < 400);
	}

	public <T> Optional<T> get(String path, String destination, TypeReference<T> type,
			Predicate<Integer> predicateStatusCode)
			throws IOException, HttpException {
		return get(path, destinationService.getHttpDestination(destination), type, predicateStatusCode);
	}

	public <T> Optional<T> get(//
			String path, HttpDestination destination, TypeReference<T> type,
			Predicate<Integer> predicateStatusCode//
	) throws IOException, HttpException {

		HttpClient httpClient = HTTP_CLIENT_FACTORY.createHttpClient(destination);
		HttpResponse response = httpClient.execute(new HttpGet(path));

		int statusCode = response.getStatusLine().getStatusCode();
		if (!predicateStatusCode.test(statusCode)) {
			String responseBody = "";
			if (response.getEntity() != null && response.getEntity().getContentLength() != 0) {
				responseBody = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
			}
			path = URLDecoder.decode(path, StandardCharsets.UTF_8);
			log.error("Failed to GET in {}. Response Status: {}. Response Body: {}", path, statusCode, responseBody);
			throw new HttpException("Failed to GET in " + path + ". Response Status: " + statusCode + ". Response Body: " + responseBody);
		}

		HttpEntity httpEntity = response.getEntity();
		if (Objects.isNull(httpEntity)) {
			throw new HttpException("Response body is empty");
		}

		if (httpEntity.getContentLength() == 0) {
			return Optional.empty();
		}
		return Optional.of(mapper.readValue(httpEntity.getContent(), type));
	}

	public <T> Optional<T> post(String path, String destination, HttpEntity entity, Class<T> responseClazz,
			Predicate<Integer> predicateStatusCode) throws IOException, HttpException {
		return post(path, destinationService.getHttpDestination(destination), entity, responseClazz,
				predicateStatusCode);
	}

	public <T> Optional<T> post(String path, HttpDestination destination, HttpEntity entity, Class<T> responseClazz,
			Predicate<Integer> predicateStatusCode) throws IOException, HttpException {
		return post(path, destination, entity, responseClazz, predicateStatusCode, mapper);
	}

	public <T> Optional<T> post(
			String path, String destination, HttpEntity entity,
			Class<T> responseClazz, Predicate<Integer> predicateStatusCode,
			ObjectMapper mapper, Header... headers) throws IOException, HttpException {

		return post(
				path,
				destinationService.getHttpDestination(destination),
				entity,
				responseClazz,
				predicateStatusCode,
				mapper,
				headers//
		);
	}

	public <T> Optional<T> post(String path, HttpDestination destination, HttpEntity entity, Class<T> responseClazz,
			Predicate<Integer> predicateStatusCode, ObjectMapper mapper, Header... headers)
			throws IOException, HttpException {

		HttpClient httpClient = HttpClientAccessor.getHttpClient(destination);

		HttpPost post = new HttpPost(path);
		post.addHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
		post.addHeader(HttpHeaders.ACCEPT_CHARSET, StandardCharsets.UTF_8.name());
		for (var header : headers) {
			post.addHeader(header);
		}
		post.setEntity(entity);

		HttpResponse response = httpClient.execute(post);

		int statusCode = response.getStatusLine().getStatusCode();
		if (!predicateStatusCode.test(statusCode)) {
			String responseBody = "";
			if (response.getEntity() != null && response.getEntity().getContentLength() != 0) {
				responseBody = new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
			}
			path = URLDecoder.decode(path, StandardCharsets.UTF_8);

			log.error("Failed to POST in {}. Response Status: {}. Response Body: {}", path, statusCode, responseBody);
			throw new HttpException("Failed to POST in " + path + ". Response Status: " + statusCode);
		}

		HttpEntity httpEntity = response.getEntity();
		if (Objects.isNull(httpEntity)) {
			throw new HttpException("Response body is empty");
		}

		if (httpEntity.getContentLength() == 0) {
			return Optional.empty();
		}

		return Optional.of(mapper.readValue(httpEntity.getContent(), responseClazz));
	}
}
