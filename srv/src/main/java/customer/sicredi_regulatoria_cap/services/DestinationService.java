package customer.sicredi_regulatoria_cap.services;

import org.springframework.stereotype.Service;

import com.sap.cloud.sdk.cloudplatform.connectivity.Destination;
import com.sap.cloud.sdk.cloudplatform.connectivity.DestinationAccessor;
import com.sap.cloud.sdk.cloudplatform.connectivity.HttpDestination;

@Service
public class DestinationService {

    public Destination getDestination(String destination) {
        return DestinationAccessor.getDestination(destination);
    }

    public HttpDestination getHttpDestination(String destination) {
        return getDestination(destination).asHttp();
    }

    
}
