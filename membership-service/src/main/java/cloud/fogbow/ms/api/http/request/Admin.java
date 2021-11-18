package cloud.fogbow.ms.api.http.request;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.ms.api.http.CommonKeys;
import cloud.fogbow.ms.api.parameters.Provider;
import cloud.fogbow.ms.api.parameters.ProviderPermission;
import cloud.fogbow.ms.api.parameters.Service;
import cloud.fogbow.ms.constants.ApiDocumentation;
import cloud.fogbow.ms.constants.SystemConstants;
import cloud.fogbow.ms.core.ApplicationFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@CrossOrigin
@RestController
@RequestMapping(value = Admin.ADMIN_ENDPOINT)
@Api(description = ApiDocumentation.Admin.API)
// TODO add documentation for parameters
public class Admin {
    public static final String ADMIN_ENDPOINT = SystemConstants.SERVICE_BASE_ENDPOINT + "admin";
    public static final String RELOAD_ENDPOINT = "/reload";
    public static final String SERVICE_ENDPOINT = "/service";
    public static final String PROVIDER_ENDPOINT = "/provider";
	
    @ApiOperation(value = ApiDocumentation.Admin.RELOAD)
    @RequestMapping(value = RELOAD_ENDPOINT, method = RequestMethod.POST)
    public ResponseEntity<Boolean> reload(
    				@ApiParam(value = cloud.fogbow.common.constants.ApiDocumentation.Token.SYSTEM_USER_TOKEN)
    				@RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken) throws FogbowException {
        ApplicationFacade.getInstance().reload(systemUserToken);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @ApiOperation(value = ApiDocumentation.Admin.SERVICE)
    @RequestMapping(value = SERVICE_ENDPOINT, method = RequestMethod.POST)
    public ResponseEntity<Boolean> service(
                    @ApiParam(value = cloud.fogbow.common.constants.ApiDocumentation.Token.SYSTEM_USER_TOKEN)
                    @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY)String systemUserToken,
                    @RequestBody Service service) throws FogbowException {
        ApplicationFacade.getInstance().updateMembershipService(systemUserToken, service.getClassName());
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @ApiOperation(value = ApiDocumentation.Admin.ADD_PROVIDER)
    @RequestMapping(value = PROVIDER_ENDPOINT, method = RequestMethod.POST)
    public ResponseEntity<Boolean> addProvider(
    				@ApiParam(value = cloud.fogbow.common.constants.ApiDocumentation.Token.SYSTEM_USER_TOKEN)
    				@RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
    				@RequestBody ProviderPermission provider) throws FogbowException {
        ApplicationFacade.getInstance().addProvider(systemUserToken, provider);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @ApiOperation(value = ApiDocumentation.Admin.REMOVE_PROVIDER)
    @RequestMapping(value = PROVIDER_ENDPOINT, method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> removeProvider(
    				@ApiParam(value = cloud.fogbow.common.constants.ApiDocumentation.Token.SYSTEM_USER_TOKEN)
    				@RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
    				@RequestBody Provider provider) throws FogbowException {
    	ApplicationFacade.getInstance().removeProvider(systemUserToken, provider.getProvider());
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @ApiOperation(value = ApiDocumentation.Admin.UPDATE_PROVIDER)
    @RequestMapping(value = PROVIDER_ENDPOINT, method = RequestMethod.PUT)
    public ResponseEntity<Boolean> updateProvider(
                    @ApiParam(value = cloud.fogbow.common.constants.ApiDocumentation.Token.SYSTEM_USER_TOKEN)
                    @RequestHeader(required = false, value = CommonKeys.SYSTEM_USER_TOKEN_HEADER_KEY) String systemUserToken,
                    @RequestBody ProviderPermission provider) throws FogbowException {
        ApplicationFacade.getInstance().updateProvider(systemUserToken, provider);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
