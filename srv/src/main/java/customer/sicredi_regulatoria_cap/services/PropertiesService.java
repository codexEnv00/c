package customer.sicredi_regulatoria_cap.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PropertiesService {

    @Value("${destination.cloud.api:CLOUD_INTEGRATION_API}")
    private String cloudDestinationApi;

    public String getCloudDestinationApi() {
        return cloudDestinationApi;
    }
}
