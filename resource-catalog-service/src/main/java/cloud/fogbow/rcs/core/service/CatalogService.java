package cloud.fogbow.rcs.core.service;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.google.common.annotations.VisibleForTesting;

import cloud.fogbow.common.constants.HttpMethod;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.UnexpectedException;
import cloud.fogbow.common.util.connectivity.HttpRequestClient;
import cloud.fogbow.common.util.connectivity.HttpResponse;
import cloud.fogbow.rcs.constants.ConfigurationPropertyKeys;
import cloud.fogbow.rcs.constants.Messages;
import cloud.fogbow.rcs.core.PropertiesHolder;
import cloud.fogbow.rcs.core.exceptions.NoSuchMemberException;
import cloud.fogbow.rcs.core.intercomponent.xmpp.requesters.RemoteGetAllServicesRequest;
import cloud.fogbow.rcs.core.intercomponent.xmpp.requesters.RemoteGetServiceRequest;
import cloud.fogbow.rcs.core.models.MembershipServiceResponse;
import cloud.fogbow.rcs.core.models.Service;
import cloud.fogbow.rcs.core.models.ServiceType;
import cloud.fogbow.rcs.core.service.cache.CacheServiceHolder;

public class CatalogService {
    
    private static final Logger LOGGER = Logger.getLogger(CatalogService.class);
    
    public static final String DOC_ENDPOINT = "/doc";
    public static final String FORMAT_SERVICE_S_PORT_KEY = "%s_port";
    public static final String FORMAT_SERVICE_S_URL_KEY = "%s_url";
    public static final String KEY_SEPARATOR = "-";
    public static final String MEMBERSHIP_SERVICE_ENDPOINT = "/ms/members";
    public static final String PORT_SEPARATOR = ":";
    public static final String SERVICE_ENDPOINT_FORMAT = "/rcs/services/%s/%s";
    public static final String SERVICE_URL_FORMAT = "%s:%s/v2/api-docs";
    public static final String URL_PREFFIX_ADDRESS = "https://";
    public static final String LOCAL_CATALOG_URL_FORMAT = "%s:%s/swagger-ui.html";

    private static final int FIRST_POSITION = 0;

    private final String SERVICE_PROPERTY_SEPARATOR = "_";
    private final String FNS_SERVICE_PROPERTY = "fns_url";
    private final String RAS_SERVICE_PROPERTY = "ras_url";
    private final String MS_SERVICE_PROPERTY = "ms_url";
    private final String AS_SERVICE_PROPERTY = "as_url";

    public List<String> requestMembers() throws FogbowException {
        String endpoint = getServiceEndpoint();
        HttpResponse content = doGetRequest(endpoint);
        MembershipServiceResponse response = getResponseFrom(content); 
        return listMembersFrom(response);
    }
    
    public List<Service> getMemberServices(String member) throws FogbowException {
        List<Service> services = new ArrayList<>();
        if (member.equals(getLocalMember())) {
            services.addAll(getLocalCatalog());
        } else {
            services.addAll(getRemoteCatalogFrom(member));
        }
        return services;
    }

    public String getServiceCatalog(String member, String service) throws FogbowException {
        String memberServiceKey = member.concat(KEY_SEPARATOR).concat(service);
        ServiceType serviceType = ServiceType.valueOf(service.toUpperCase());
        boolean hasCached = CacheServiceHolder.getInstance().getCacheService().has(memberServiceKey);
        if (!hasCached) {
            RemoteGetServiceRequest remoteRequest = RemoteGetServiceRequest.builder()
                    .member(member)
                    .serviceType(serviceType)
                    .build();
            
            remoteRequest.send();
        }
        return CacheServiceHolder.getInstance().getCacheService().get(memberServiceKey);
    }
    
    public HttpResponse requestService(String member, ServiceType serviceType) {
        HttpResponse response = null;
        try {
            String url = getServiceUrl(serviceType);
            String port = getServicePort(serviceType);
            String serviceUrl = String.format(SERVICE_URL_FORMAT, url, port);
            response = doGetRequest(serviceUrl);
        } catch (Exception e) {
            LOGGER.error(String.format(Messages.Error.ERROR_WHILE_GETTING_SERVICE_S_FROM_MEMBER_S, 
                    serviceType.name(), member), e);
        }
        return response;
    }
    
    public void cacheSave(String key, String content) {
        try {
            CacheServiceHolder.getInstance().getCacheService().set(key, content);
        } catch (FogbowException e) {
            LOGGER.error(Messages.Error.ERROR_TRYING_TO_SAVE, e);
        }
    }

    @VisibleForTesting
    List<Service> getRemoteCatalogFrom(String member) throws FogbowException{
        List<String> members = requestMembers();
        if(!members.contains(member)) {
            throw new NoSuchMemberException(String.format(Messages.Exception.NO_SUCH_MEMBER, member));
        }

        List<Service> services = new ArrayList<>();
        List<ServiceType> serviceTypes = RemoteGetAllServicesRequest.builder().member(member).build().send();
        for (ServiceType serviceType : serviceTypes) {
            String endpoint = String.format(SERVICE_ENDPOINT_FORMAT, member, serviceType.getName());
            Service service = new Service(serviceType, endpoint);
            services.add(service);
        }
        return services;
    }

    @VisibleForTesting
    List<Service> getLocalCatalog() {
        List<ServiceType> serviceTypes = this.getServices();
        List<Service> services = new ArrayList<>();

        for (ServiceType type : serviceTypes) {
            String location = String.format(LOCAL_CATALOG_URL_FORMAT, this.getServiceUrl(type), this.getServicePort(type));
            services.add(new Service(type, location));
        }

        return services;
    }
    
    @VisibleForTesting
    List<String> listMembersFrom(MembershipServiceResponse response) {
        List<String> members = new ArrayList<>();
        String localMember = getLocalMember();
        for (String member : response.getMembers()) {
            if (member.equals(localMember)) {
                // add local member always at beginning of the list
                members.add(FIRST_POSITION, member);
            } else {
                members.add(member);
            }
        }
        return members;
    }
    
    @VisibleForTesting
    MembershipServiceResponse getResponseFrom(HttpResponse httpResponse) {
        String content = httpResponse.getContent();
        return MembershipServiceResponse.fromJson(content);
    }
    
    @VisibleForTesting
    HttpResponse doGetRequest(String endpoint) throws UnexpectedException {
        Map<String, String> headers = new HashMap<>();
        Map<String, String> body = new HashMap<>();
        try {
            return HttpRequestClient.doGenericRequest(HttpMethod.GET, endpoint, headers, body);
        } catch (Exception e) {
            String message = String.format(Messages.Exception.GENERIC_EXCEPTION, e.getMessage());
            throw new UnexpectedException(message, e);
        }
    }
    
    @VisibleForTesting
    String getServicePort(ServiceType serviceType) {
        return PropertiesHolder.getInstance().getProperty(String.format(FORMAT_SERVICE_S_PORT_KEY, serviceType.getName()));
    }
    
    @VisibleForTesting
    String getServiceUrl(ServiceType serviceType) {
        return PropertiesHolder.getInstance().getProperty(String.format(FORMAT_SERVICE_S_URL_KEY, serviceType.getName()));
    }

    @VisibleForTesting
    String getServiceEndpoint() {
        String msUrl = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.MEMBERSHIP_SERVICE_URL_KEY);
        String msPort = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.MEMBERSHIP_SERVICE_PORT_KEY);

        return msUrl.concat(PORT_SEPARATOR).concat(msPort).concat(MEMBERSHIP_SERVICE_ENDPOINT);
    }

    @VisibleForTesting
    String getLocalMember() {
        return PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.LOCAL_MEMBER_ID_KEY);
    }

    public List<ServiceType> getServices() {
        List<String> possibleServicesProperties = new ArrayList<String>(){
            {
                add(FNS_SERVICE_PROPERTY);
                add(RAS_SERVICE_PROPERTY);
                add(AS_SERVICE_PROPERTY);
                add(MS_SERVICE_PROPERTY);
            }
        };

        List<ServiceType> services = new ArrayList<>();

        for(String serviceProperty : possibleServicesProperties) {
            String propertyValue = PropertiesHolder.getInstance().getProperty(serviceProperty);
            if(propertyValue != null && !propertyValue.trim().isEmpty()) {
                ServiceType currentServiceType = ServiceType.valueOf(serviceProperty.split(SERVICE_PROPERTY_SEPARATOR)[0].toUpperCase());
                services.add(currentServiceType);
            }
        }

        return services;
    }
}
