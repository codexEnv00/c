package customer.sicredi_regulatoria_cap.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.sap.cds.ql.Insert;

import cds.gen.sicredi.db.entities.Protocols;
import cds.gen.sicredi.db.entities.Protocols_;
import customer.sicredi_regulatoria_cap.factory.ProtocolTestFactory;

public class ReportServiceTest extends BaseIntegrationTest {

	@Nested
	class RequestProtocols {

		private List<Protocols> protocols;

		@BeforeEach
		void onBeforeEach() {
			protocols = ProtocolTestFactory.getList(() -> ProtocolTestFactory.getValid());
			db.run(Insert.into(Protocols_.class).entries(protocols));
		}

		@AfterEach
		void onAfterEach() {
			//db.run(Delete.from(Protocols_.class));
		}

		@Test
		void shouldReturnProtocolData_WhenRequestProtocols() throws Exception {
			var protocol = protocols.get(0);
			var result = client.perform(
				MockMvcRequestBuilders.get("/odata/v4/report/Protocols")
				.with(SecurityMockMvcRequestPostProcessors.httpBasic("system", ""))
			)
			.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
			.andExpect(MockMvcResultMatchers.jsonPath("$.value").isArray())
			.andExpect(MockMvcResultMatchers.jsonPath("$.value.length()").value(protocols.size()))

			// .andExpect(MockMvcResultMatchers.jsonPath("$.value[0].bloc").value(protocol.getBloc()))
			// .andExpect(MockMvcResultMatchers.jsonPath("$.value[0].cadoc").value(protocol.getCadoc()))
			// .andExpect(MockMvcResultMatchers.jsonPath("$.value[0].company_externalCode").value(protocol.getCompanyExternalCode()))
			// .andExpect(MockMvcResultMatchers.jsonPath("$.value[0].createdAt").value(protocol.getCreatedAt()))
			// .andExpect(MockMvcResultMatchers.jsonPath("$.value[0].createdBy").value(protocol.getCreatedBy()))
			// .andExpect(MockMvcResultMatchers.jsonPath("$.value[0].exercise").value(protocol.getExercise()))
			// .andExpect(MockMvcResultMatchers.jsonPath("$.value[0].ID").value(protocol.getId()))
			// .andExpect(MockMvcResultMatchers.jsonPath("$.value[0].interval").value(protocol.getInterval()))
			// .andExpect(MockMvcResultMatchers.jsonPath("$.value[0].modifiedAt").value(protocol.getModifiedAt()))
			// .andExpect(MockMvcResultMatchers.jsonPath("$.value[0].modifiedBy").value(protocol.getModifiedBy()))
			// .andExpect(MockMvcResultMatchers.jsonPath("$.value[0].protocol").value(protocol.getProtocol()))
			// .andExpect(MockMvcResultMatchers.jsonPath("$.value[0].releaseDate").value(protocol.getReleaseDate()))
			// .andExpect(MockMvcResultMatchers.jsonPath("$.value[0].status").value(protocol.getStatus()))
			// .andExpect(MockMvcResultMatchers.jsonPath("$.value[0].tipoDoc").value(protocol.getTipoDoc()))
			// .andExpect(MockMvcResultMatchers.jsonPath("$.value[0].version").value(protocol.getVersion()))
			.andReturn();
			
			mapper.findAndRegisterModules();
			mapper.disable(DeserializationFeature.UNWRAP_ROOT_VALUE);

			String json = result.getResponse().getContentAsString();
			JsonNode rootNode = mapper.readTree(json);
			JsonNode valueNode = rootNode.path("value");

			for(int i = 0; i < valueNode.size(); i++) {
				assertEquals(valueNode.get(i).get("bloc"), protocols.get(i).getBloc().toString());
				assertEquals(valueNode.get(i).get("releaseDate"), protocols.get(i).getReleaseDate());
				assertEquals(valueNode.get(i).get("exercise"), protocols.get(i).getExercise());
				assertEquals(valueNode.get(i).get("tipoDoc"), protocols.get(i).getTipoDoc().toString());
				assertEquals(valueNode.get(i).get("cadoc"), protocols.get(i).getCadoc());
				assertEquals(valueNode.get(i).get("interval"), protocols.get(i).getInterval());
				assertEquals(valueNode.get(i).get("company_externalCode"), protocols.get(i).getCompanyExternalCode());
				assertEquals(valueNode.get(i).get("version"), protocols.get(i).getVersion());
				assertEquals(valueNode.get(i).get("status"), protocols.get(i).getStatus());
			}
		
			// JsonNode expectedNode = mapper.valueToTree(protocols);

		
			// if (!valueNode.equals(expectedNode)) {
			// 	throw new AssertionError("A resposta não bate com a lista de protocols esperada");
			// }
		}
	}
}
