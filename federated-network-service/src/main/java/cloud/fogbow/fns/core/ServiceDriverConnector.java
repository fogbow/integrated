package cloud.fogbow.fns.core;

import cloud.fogbow.fns.core.drivers.ServiceDriver;

public class ServiceDriverConnector {
    private ServiceDriver driver;

    public ServiceDriverConnector(String serviceName) {
        this.driver = new ServiceDriverInstantiator().getDriver(serviceName);
    }

    public ServiceDriver getDriver() {
        return driver;
    }

    public void setDriver(ServiceDriver driver) {
        this.driver = driver;
    }
}
