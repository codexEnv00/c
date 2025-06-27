package customer.sicredi_regulatoria_cap.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.services.persistence.PersistenceService;

import cds.gen.sicredi.db.entities.TerminationDocument;
import cds.gen.sicredi.db.entities.TerminationDocument_;
import customer.sicredi_regulatoria_cap.factory.TerminationDocumentTestFactory;

@AutoConfigureMockMvc
@SpringBootTest
public class BalanceServiceHandlerTest {

	@Autowired
	private MockMvc client;

	@Autowired
	private PersistenceService db;

	@BeforeEach
	public void loadInitialData() {
		db.run(Insert.into(TerminationDocument_.class).entries(
			TerminationDocumentTestFactory.getList(() -> TerminationDocumentTestFactory.getValid())
		));
	}

	@AfterEach
	public void cleanUpData() {
		db.run(Delete.from(TerminationDocument_.class));
	}

	@Test
	void test() throws Exception {
		var terminations = db.run(Select.from(TerminationDocument_.class));
		System.out.println(terminations);
		var termination = db.run(Select.from(TerminationDocument_.class)).first(TerminationDocument.class).get();

		MvcResult result = client.perform(
			get("/odata/v4/balance/TerminationDocument")
			.with(httpBasic("system", ""))
			.queryParam("cadoc", termination.getCadoc())
			.queryParam("companies", termination.getCompanyExternalCode())
			.queryParam("exercise", termination.getExercise())
			.queryParam("version", "SI25")
			.queryParam("interval", termination.getInterval())
		)
		.andReturn();

		String response = result.getResponse().getContentAsString();
		System.out.println(response);
	}
}
