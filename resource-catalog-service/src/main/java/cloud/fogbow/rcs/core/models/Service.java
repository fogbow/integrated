package cloud.fogbow.rcs.core.models;

public class Service {

    private ServiceType serviceType;
    private String location;
    
    public Service() {
    }
    
    public Service(ServiceType serviceType, String location) {
        this.serviceType = serviceType;
        this.location = location;
    }

    /**
     * @return the representation of the enum in a string.
     */
    public String getServiceType() {
        return serviceType.getName();
    }
    
    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
}
