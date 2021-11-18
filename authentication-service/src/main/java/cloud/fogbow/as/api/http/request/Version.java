package cloud.fogbow.as.api.http.request;

import cloud.fogbow.as.constants.SystemConstants;
import cloud.fogbow.as.core.ApplicationFacade;
import cloud.fogbow.common.constants.ApiDocumentation;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping(value = Version.VERSION_ENDPOINT)
@Api(description = ApiDocumentation.Version.API)
public class Version {

    public static final String VERSION_ENDPOINT = SystemConstants.SERVICE_BASE_ENDPOINT + "version";

    private final Logger LOGGER = Logger.getLogger(Version.class);

    @ApiOperation(value = ApiDocumentation.Version.GET_OPERATION)
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<cloud.fogbow.as.api.http.response.Version> getVersion() {

        String versionNumber = ApplicationFacade.getInstance().getVersionNumber();
        cloud.fogbow.as.api.http.response.Version version = new cloud.fogbow.as.api.http.response.Version(versionNumber);
        return new ResponseEntity<>(version, HttpStatus.OK);
    }
}
