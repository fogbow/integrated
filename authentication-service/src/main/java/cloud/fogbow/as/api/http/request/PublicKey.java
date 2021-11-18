package cloud.fogbow.as.api.http.request;

import cloud.fogbow.as.constants.SystemConstants;
import cloud.fogbow.common.constants.ApiDocumentation;
import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.as.core.ApplicationFacade;
import cloud.fogbow.as.constants.Messages;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = PublicKey.PUBLIC_KEY_ENDPOINT)
@Api(description = ApiDocumentation.PublicKey.API)
public class PublicKey {
    public static final String PUBLIC_KEY_ENDPOINT = SystemConstants.SERVICE_BASE_ENDPOINT + "publicKey";

    private final Logger LOGGER = Logger.getLogger(PublicKey.class);

    @ApiOperation(value = ApiDocumentation.PublicKey.GET_OPERATION)
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<cloud.fogbow.as.api.http.response.PublicKey> getPublicKey() throws FogbowException {
        try {
            LOGGER.info(Messages.Log.RECEIVING_GET_PUBLIC_KEY_REQUEST);
            String publicKeyValue = ApplicationFacade.getInstance().getPublicKey();
            cloud.fogbow.as.api.http.response.PublicKey publicKey = new cloud.fogbow.as.api.http.response.PublicKey(publicKeyValue);
            return new ResponseEntity<>(publicKey, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.info(String.format(Messages.Log.OPERATION_RETURNED_ERROR_S, e.getMessage()), e);
            throw new InternalServerErrorException(e.getMessage());
        }
    }
}
