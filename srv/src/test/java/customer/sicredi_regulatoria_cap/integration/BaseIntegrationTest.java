package customer.sicredi_regulatoria_cap.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import com.sap.cds.services.persistence.PersistenceService;

@AutoConfigureMockMvc
@SpringBootTest
public abstract class BaseIntegrationTest {
	@Autowired
	protected MockMvc client;

	@Autowired
	protected PersistenceService db;

	@Autowired
	protected ObjectMapper mapper;

    protected Faker faker = new Faker();
}
