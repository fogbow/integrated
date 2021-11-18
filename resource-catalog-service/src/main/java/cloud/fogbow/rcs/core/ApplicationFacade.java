package cloud.fogbow.rcs.core;

import java.nio.file.Paths;
import java.util.List;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.UnexpectedException;
import cloud.fogbow.common.util.BashScriptRunner;
import cloud.fogbow.rcs.constants.ConfigurationPropertyDefaults;
import cloud.fogbow.rcs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.rcs.constants.SystemConstants;
import cloud.fogbow.rcs.core.models.Service;
import cloud.fogbow.rcs.core.service.cache.CacheServiceHolder;
import com.google.common.annotations.VisibleForTesting;
import cloud.fogbow.rcs.core.service.CatalogService;
import cloud.fogbow.rcs.core.service.cache.MemoryBasedCache;

public class ApplicationFacade {
    
    public static final String BUILD_NUMBER_FORMAT = "%s-%s";
    private static final String SEPARATOR = "-";

    private static ApplicationFacade instance;
    private CatalogService catalogService;
    private String buildNumber;

    private final String UPDATE_PROPERTIES_SCRIPT_PATH = "/bin/update-properties.sh";
    private final String UPDATE_PROPERTIES_SCRIPT_WHOLE_PATH = Paths.get("").toAbsolutePath().toString() + UPDATE_PROPERTIES_SCRIPT_PATH;
    private final String UNABLE_TO_UPDATE_PROPERTY = "Unable to update property";

    private ApplicationFacade() {
        this.buildNumber = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.BUILD_NUMBER_KEY,
                String.format(BUILD_NUMBER_FORMAT, SystemConstants.API_VERSION_NUMBER,
                        ConfigurationPropertyDefaults.BUILD_NUMBER));
    }

    public static ApplicationFacade getInstance() {
        synchronized (ApplicationFacade.class) {
            if (instance == null) {
                instance = new ApplicationFacade();
            }
            return instance;
        }
    }
    
    public List<String> getMembers() throws FogbowException {
        return this.catalogService.requestMembers();
    }
    
    public List<Service> getServicesFrom(String member) throws FogbowException {
        return this.catalogService.getMemberServices(member);
    }
    
    public String getService(String member, String service) throws FogbowException {
        return this.catalogService.getServiceCatalog(member, service);
    }
    
    // version request
    public String getVersionNumber() {
        return this.buildNumber;
    }

    public synchronized void setCatalogService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public void updateCacheExpiration(String newExpiration) throws Exception{
        BashScriptRunner runner = new BashScriptRunner();

        String[] command = {"bash", UPDATE_PROPERTIES_SCRIPT_WHOLE_PATH, ConfigurationPropertyKeys.CACHE_EXPIRATION_TIME_KEY, newExpiration};
        BashScriptRunner.Output result = runner.runtimeRun(command);

        if(result.getExitCode() != 0) {
            throw new UnexpectedException(UNABLE_TO_UPDATE_PROPERTY);
        }

        PropertiesHolder.getInstance().refreshProperties();

        int newCacheExpiration = Integer.parseInt(PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.CACHE_EXPIRATION_TIME_KEY));
        ((MemoryBasedCache) CacheServiceHolder.getInstance().getCacheService()).setCacheExpiration(newCacheExpiration);
    }
    
    public void removeCache(String member, String service) {
        String memberServiceKey = member.concat(SEPARATOR).concat(service);
        CacheServiceHolder.getInstance().getCacheService().unset(memberServiceKey);
    }
}