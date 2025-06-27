package customer.sicredi_regulatoria_cap.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.http.HttpException;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;

import cds.gen.sicredi.db.entities.Companies_;
import cds.gen.sicredi.db.entities.CompanyAuthenticators;
import cds.gen.sicredi.db.entities.cadoc4500.Estban;
import cds.gen.sicredi.db.entities.cadoc4500.Estban_;
import customer.sicredi_regulatoria_cap.dtos.WrapperResultDTO;
import customer.sicredi_regulatoria_cap.dtos.bacen.Estban4500DTO;
import customer.sicredi_regulatoria_cap.dtos.bacen.ResponseSendBacenDTO;
import customer.sicredi_regulatoria_cap.factory.CompanyTestFactory;
import customer.sicredi_regulatoria_cap.factory.Estban4500TestFactory;
import customer.sicredi_regulatoria_cap.services.APIDestinationService;
import customer.sicredi_regulatoria_cap.services.CompanyService;

public class Cadoc4500ServiceHandlerTest extends BaseIntegrationTest {

	@MockBean
	private CompanyService companyService;

	@MockBean
	private APIDestinationService apiService;

	private List<Estban> estbans;

	@BeforeEach
	public void loadInitialData() throws IOException, HttpException {

		Mockito.clearInvocations(apiService, companyService);

		Mockito.when(apiService.get(Mockito.startsWith("http/cadoc4500/getCadocSet"), Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(
			Optional.of(new WrapperResultDTO<Estban4500DTO>(Arrays.asList()))
		);

		var companyAuth = CompanyAuthenticators.create();
		companyAuth.setCompanyExternalCode("12345");
		companyAuth.setUser(faker.name().username());
		companyAuth.setPassword(faker.internet().password());

		Mockito.when(companyService.getCompanyAuthenticator(Mockito.any())).thenReturn(
			Optional.of(companyAuth)
		);

		estbans = Estban4500TestFactory.getList(() -> Estban4500TestFactory.getValid());
		db.run(Insert.into(Estban_.class).entries(estbans));
	}

	@AfterEach
	public void cleanUpData() {
		db.run(Delete.from(Estban_.class));
	}

	@Nested
	class ReadEntityEstban {		
		@Test
		void shouldReturnEstban4500Correctly_WhenRequestEntityEstban_WithValidFilter() throws Exception {

			var estban = estbans.get(0);

			final String filter = MessageFormat.format("company/externalCode in (''{0}'') and interval eq ''{1}'' and exercise eq ''{2}''", estban.getCompanyExternalCode(), estban.getInterval(), estban.getExercise());

			client.perform(
				MockMvcRequestBuilders.get("/odata/v4/cadoc/4500/Estban")
				.with(SecurityMockMvcRequestPostProcessors.httpBasic("system", ""))
				.queryParam("$filter", filter)
			)
			.andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.value").exists())
			.andExpect(MockMvcResultMatchers.jsonPath("$.value").isArray())
			.andExpect(MockMvcResultMatchers.jsonPath("$.value.length()").value(1))
			
			.andExpect(MockMvcResultMatchers.jsonPath("$.value[0].nodeID").value(estban.getNodeID()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.value[0].hierarchyLevel").value(0))
			.andExpect(MockMvcResultMatchers.jsonPath("$.value[0].parentNodeID").value(IsNull.nullValue()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.value[0].drillState").value(estban.getDrillState()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.value[0].company_externalCode").value(estban.getCompanyExternalCode()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.value[0].interval").value(estban.getInterval()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.value[0].exercise").value(estban.getExercise()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.value[0].agency").value(estban.getAgency()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.value[0].account").value(estban.getAccount()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.value[0].description").value(estban.getDescription()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.value[0].balance").value(estban.getBalance()));
			
			verify(companyService, times(1)).loadCompanies();
			verify(apiService, times(1)).get(anyString(), anyString(), any(), any());
		}
		
		@Test
		void shouldReturnEmptyEstban4500_WhenRequestEntityEstban_WithNonExistingFilter() throws Exception {
			
			var estban = estbans.get(0);
			
			final String filter = MessageFormat.format("company/externalCode in (''123456789'') and interval eq ''{0}'' and exercise eq ''{1}''", estban.getInterval(), estban.getExercise());
			
			client.perform(
				MockMvcRequestBuilders.get("/odata/v4/cadoc/4500/Estban")
				.with(SecurityMockMvcRequestPostProcessors.httpBasic("system", ""))
				.queryParam("$filter", filter)
			)
			.andExpect(MockMvcResultMatchers.status().is(HttpStatus.OK.value()))
			.andExpect(MockMvcResultMatchers.jsonPath("$.value").exists())
			.andExpect(MockMvcResultMatchers.jsonPath("$.value").isArray())
			.andExpect(MockMvcResultMatchers.jsonPath("$.value.length()").value(0));

			verify(companyService, times(1)).loadCompanies();
			verify(apiService, times(1)).get(anyString(), anyString(), any(), any());
		}

	}

	@Nested
	class TransmitBacen {

		record TransmitBacenContent(
			List<String> companies, String exercise, String interval,
			String user, String remittanceType
		) {};

		@BeforeEach
		public void onBeforeEach() throws IOException, HttpException {
			Mockito.clearInvocations(apiService);

			Mockito.when(
				apiService.post(
					Mockito.eq("/http/Cap_In_Bacen_DataTransfer4500"), 
					Mockito.anyString(), 
					Mockito.any(), 
					Mockito.any(), 
					Mockito.any(), 
					Mockito.any(), 
					Mockito.any()
				)
			).thenReturn(Optional.of(new ResponseSendBacenDTO("123456")));
		}

		@Test
		void shouldTransmitToBacen_WhenPostTransmitBacen_WithValidData() throws Exception {
			var estban = estbans.get(0);

			db.run(
				Insert.into(Companies_.class)
				.entry(CompanyTestFactory.getValid(estban.getCompanyExternalCode()))
			);

			final var content = new TransmitBacenContent(
				Arrays.asList(estban.getCompanyExternalCode()), 
				estban.getExercise(), 
				estban.getInterval(), 
				faker.name().fullName(), 
				faker.options().nextElement(Arrays.asList("I", "S"))
			);

			client.perform(
				MockMvcRequestBuilders.post("/odata/v4/cadoc/4500/transmitBacen")
				.with(SecurityMockMvcRequestPostProcessors.httpBasic("system", ""))
				.content(mapper.writeValueAsString(content))
				.contentType(MediaType.APPLICATION_JSON)
			)
			.andExpect(MockMvcResultMatchers.status().is2xxSuccessful())
			.andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Accounts sent"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.sent").value(true))
			.andExpect(MockMvcResultMatchers.jsonPath("$.userConfirmation").value(false));

			verify(apiService, times(1)).post(
				eq("/http/Cap_In_Bacen_DataTransfer4500"), 
				anyString(), any(), any(), any(), any(), any()
			);
		}
	}
	
}
