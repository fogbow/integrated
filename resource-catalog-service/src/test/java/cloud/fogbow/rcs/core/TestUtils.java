package cloud.fogbow.rcs.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.dom4j.Element;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.xmpp.packet.IQ;

import cloud.fogbow.rcs.core.intercomponent.RemoteFacade;
import cloud.fogbow.rcs.core.intercomponent.xmpp.IqElement;
import cloud.fogbow.rcs.core.intercomponent.xmpp.RemoteMethod;
import cloud.fogbow.rcs.core.intercomponent.xmpp.requesters.RemoteGetServiceRequest;
import cloud.fogbow.rcs.core.models.Service;
import cloud.fogbow.rcs.core.models.ServiceType;
import cloud.fogbow.rcs.core.service.CatalogService;
import cloud.fogbow.rcs.core.service.cache.CacheService;
import cloud.fogbow.rcs.core.service.cache.CacheServiceHolder;

public class TestUtils {
    
    private static final String EMPTY_STRING = "";
    private static final String MEMBERS_LIST_JSON_FILE_NAME = "membersList.json";
    private static final String SWAGGER_API_JSON_FILE_NAME = "sampleSwaggerApi.json";
    private static final String CATALOG_HTML_FILE_NAME = "catalog.html";
    private static final String RESOURCES_API_HTTP_RESPONSE_PATH = "cloud/fogbow/rcs/api/http/response/";
    private static final String SERVICES_LIST_JSON_FILE_NAME = "localServicesList.json";
    private static final String VERSION_NUMBER_JSON_FILE_NAME = "versionNumber.json";
    
    public static final String[] MEMBERS = { "member1", "member2", "member3" };
    public static final String[] SERVICES = { "ras", "as", "fns" };
    public static final String BASE_URL = "/";
    public static final String DEFAULT_PORT = "8080";
    public static final String FAKE_CONTENT_JSON = "{content:\"anything\"}";
    public static final String FAKE_LOCAL_MEMBER_URL = "https://member1.org/doc";
    public static final String FAKE_MEMBER_SERVICE_KEY = "member1-ms";
    public static final String FAKE_REMOTE_MEMBER_URL = "https://member2.org/doc";
    public static final String FAKE_SENDER_ID = "rcs-member1";
    public static final String LOCALHOST_URL = "http://localhost";
    public static final String MEMBERSHIP_SERVICE_ENDPOINT = "http://localhost:8080/ms/members";
    public static final String MEMBERSHIP_SERVICE_RESPONSE_JSON = "{\"members\": [\"member1\",\"member2\",\"member3\"]}";
    public static final String HANG_ON = "1";
    public static final String NOT_WAIT = "0";
    
    
    public static final int LOCAL_MEMBER_INDEX = 0;
    public static final int REMOTE_MEMBER_INDEX = 1;
    public static final int RUN_ONCE = 1;
    public static final int RUN_TWICE = 2;

    public static final int FIRST_ARRAY_POSITION = 0;
    public static final int SECOND_ARRAY_POSITION = 1;

    public ApplicationFacade mockApplicationFacade() {
        ApplicationFacade facade = Mockito.mock(ApplicationFacade.class);
        PowerMockito.mockStatic(ApplicationFacade.class);
        BDDMockito.given(ApplicationFacade.getInstance()).willReturn(facade);
        return facade;
    }
    
    public RemoteFacade mockRemoteFacade() {
        RemoteFacade facade = Mockito.spy(RemoteFacade.getInstance());
        PowerMockito.mockStatic(RemoteFacade.class);
        BDDMockito.given(RemoteFacade.getInstance()).willReturn(facade);
        facade.setCatalogService(new CatalogService());
        return facade;
    }
    
    public RemoteGetServiceRequest mockRemoteServiceRequestBuilder(String member, ServiceType serviceType) {
        RemoteGetServiceRequest.Builder requestBuilder = Mockito.mock(RemoteGetServiceRequest.Builder.class);
        PowerMockito.mockStatic(RemoteGetServiceRequest.class);
        BDDMockito.given(RemoteGetServiceRequest.builder()).willReturn(requestBuilder);
        Mockito.when(requestBuilder.member(Mockito.eq(member))).thenReturn(requestBuilder);
        Mockito.when(requestBuilder.serviceType(Mockito.eq(serviceType))).thenReturn(requestBuilder);
        RemoteGetServiceRequest request = Mockito.mock(RemoteGetServiceRequest.class);
        Mockito.when(requestBuilder.build()).thenReturn(request);
        return request;
    }
    
    public CacheService<String> spyCacheServiceHolder() {
        CacheServiceHolder cacheServiceHolder = Mockito.mock(CacheServiceHolder.class);
        CacheService<String> cacheService = Mockito.spy(CacheServiceHolder.getInstance().getCacheService());
        PowerMockito.mockStatic(CacheServiceHolder.class);
        BDDMockito.when(CacheServiceHolder.getInstance()).thenReturn(cacheServiceHolder);
        Mockito.when(cacheServiceHolder.getCacheService()).thenReturn(cacheService);
        return cacheService;
    }
    
    public Service createLocalService() {
        return new Service(ServiceType.AS, TestUtils.FAKE_LOCAL_MEMBER_URL);
    }

    public String getCatalogSpec() throws IOException {
        String jsonFilePath = getPathFile(RESOURCES_API_HTTP_RESPONSE_PATH.concat(SWAGGER_API_JSON_FILE_NAME));
        return readFileAsString(jsonFilePath);
    }
    
    public List<Service> createServicesList() {
        Service[] arrayService = { 
                new Service(ServiceType.AS, TestUtils.FAKE_REMOTE_MEMBER_URL),
                new Service(ServiceType.FNS, TestUtils.FAKE_REMOTE_MEMBER_URL),
                new Service(ServiceType.MS, TestUtils.FAKE_REMOTE_MEMBER_URL),
                new Service(ServiceType.RAS, TestUtils.FAKE_REMOTE_MEMBER_URL)
        };
        List<Service> services = Arrays.asList(arrayService);
        return services;
    }
    
    public IQ generateRemoteRequest(String member, String service) {
        IQ iq = new IQ();
        iq.setTo(FAKE_SENDER_ID);
        Element queryElement = iq.getElement().addElement(IqElement.QUERY.toString(),
                RemoteMethod.REMOTE_GET_SERVICE.toString());
        
        Element memberElement = queryElement.addElement(IqElement.MEMBER.toString());
        memberElement.setText(member);
        
        Element serviceElement = queryElement.addElement(IqElement.SERVICE.toString());
        serviceElement.setText(service);
        return iq;
    }
    
    public IQ getRemoteResponse() {
        IQ response = new IQ();
        Element queryElement = response.getElement().addElement(IqElement.QUERY.toString(),
                RemoteMethod.REMOTE_GET_SERVICE.toString());
        
        Element contentElement = queryElement.addElement(IqElement.CONTENT.toString());
        contentElement.setText(FAKE_CONTENT_JSON);
        return response;
    }
    
    public IQ getRemoteResponseHandle(IQ iq) {
        IQ response = new IQ(IQ.Type.result, iq.getID());
        response.setFrom(iq.getTo());
         
        Element queryElement = response.getElement().addElement(IqElement.QUERY.toString());
        Element contentElement = queryElement.addElement(IqElement.CONTENT.toString());
        contentElement.setText(FAKE_CONTENT_JSON);
         
        return response;
    }
    
    public String getLocalServicesListResponseContent() throws IOException {
        String pathFile = getPathFile(RESOURCES_API_HTTP_RESPONSE_PATH.concat(SERVICES_LIST_JSON_FILE_NAME));
        String rawJson = readFileAsString(pathFile);
        return String.format(rawJson, ServiceType.AS.getName(), FAKE_LOCAL_MEMBER_URL);
    }

    public String getMembersListResponseContent() throws IOException {
        String pathFile = getPathFile(RESOURCES_API_HTTP_RESPONSE_PATH.concat(MEMBERS_LIST_JSON_FILE_NAME));
        String rawJson = readFileAsString(pathFile);
        return String.format(rawJson, MEMBERS);
    }

    public String getServiceResponseContent() throws IOException {
        String htmlFilePath = getPathFile(RESOURCES_API_HTTP_RESPONSE_PATH.concat(CATALOG_HTML_FILE_NAME));

        String rawJson = getCatalogSpec();
        String rawHtml = readFileAsString(htmlFilePath);

        return String.format(rawHtml, rawJson);
    }

    public String getVersionNumberResponseContent() throws IOException {
        String pathFile = getPathFile(RESOURCES_API_HTTP_RESPONSE_PATH.concat(VERSION_NUMBER_JSON_FILE_NAME));
        String rawJson = readFileAsString(pathFile);
        return String.format(rawJson, ApplicationFacade.getInstance().getVersionNumber());
    }
    
    private String getPathFile(String path) {
        String rootPath = Thread.currentThread().getContextClassLoader()
                .getResource(EMPTY_STRING)
                .getPath();
        
        return rootPath.concat(path);
    }
    
    private String readFileAsString(final String fileName) throws IOException {
        Path path = Paths.get(fileName);
        byte[] bytes = Files.readAllBytes(path);
        return new String(bytes);
    }

}
