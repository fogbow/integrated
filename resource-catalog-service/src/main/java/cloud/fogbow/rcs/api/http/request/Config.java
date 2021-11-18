package cloud.fogbow.rcs.api.http.request;

import cloud.fogbow.rcs.api.http.parameters.ExpirationTimeProperty;
import cloud.fogbow.rcs.constants.ApiDocumentation;
import cloud.fogbow.rcs.constants.Messages;
import cloud.fogbow.rcs.constants.SystemConstants;
import cloud.fogbow.rcs.core.ApplicationFacade;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = Config.ENDPOINT)
@Api(ApiDocumentation.Config.API)
public class Config {

    protected static final String ENDPOINT = SystemConstants.SERVICE_BASE_ENDPOINT + SystemConstants.CONFIG_BASE_ENDPOINT;

    private static final Logger LOGGER = Logger.getLogger(Config.class);

    @ApiOperation(value = ApiDocumentation.Config.UPDATE_EXPIRATION_TIME)
    @RequestMapping(value="/expiration_time", method = RequestMethod.PUT)
    public ResponseEntity<Boolean> updateCacheExpirationTime(
            @ApiParam(value = ApiDocumentation.Config.EXPIRATION_TIME)
            @RequestBody ExpirationTimeProperty expirationTime
    ) throws Exception {
        try {
            LOGGER.info(String.format(Messages.Info.UPDATING_EXPIRATION_TIME));
            ApplicationFacade.getInstance().updateCacheExpiration(expirationTime.getValue());
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(String.format(Messages.Exception.GENERIC_EXCEPTION, e.getMessage()), e);
            throw e;
        }
    }
}
