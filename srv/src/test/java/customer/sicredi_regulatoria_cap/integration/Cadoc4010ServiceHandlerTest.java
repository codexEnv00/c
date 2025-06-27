package customer.sicredi_regulatoria_cap.integration;

import java.io.IOException;

import org.apache.http.HttpException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.sap.cds.ql.Delete;

import cds.gen.sicredi.db.entities.Balances_;
import customer.sicredi_regulatoria_cap.services.APIDestinationService;
import customer.sicredi_regulatoria_cap.services.CompanyService;

public class Cadoc4010ServiceHandlerTest extends BaseIntegrationTest {
	
	@MockBean
	private APIDestinationService apiService;

	@MockBean
	private CompanyService companyService;

	@BeforeEach
	public void loadInitialData() throws IOException, HttpException {

		Mockito.clearInvocations(apiService, companyService);

		Mockito.when(apiService.get(Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.any())).thenReturn(
			null
		);
	}

	@AfterEach
	public void cleanUpData() {
		db.run(Delete.from(Balances_.class));
	}

	@Nested
	class ReadEntityBalance {
	}
}
