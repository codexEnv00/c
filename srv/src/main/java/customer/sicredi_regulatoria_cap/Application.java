package customer.sicredi_regulatoria_cap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.sap.cloud.sdk", "customer.sicredi_regulatoria_cap", "cds.gen"})
@ServletComponentScan({"com.sap.cloud.sdk", "customer.sicredi_regulatoria_cap", "cds.gen"})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
