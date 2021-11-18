package cloud.fogbow.rcs.api.http.response;

import java.util.List;

import cloud.fogbow.rcs.constants.ApiDocumentation;
import cloud.fogbow.rcs.core.models.Service;
import io.swagger.annotations.ApiModelProperty;

public class ServicesList {

    @ApiModelProperty(example = ApiDocumentation.Model.SERVICES_LIST)
    List<Service> services;
    
    public ServicesList() {}
    
    public ServicesList(List<Service> services) {
        this.services = services;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }
}
