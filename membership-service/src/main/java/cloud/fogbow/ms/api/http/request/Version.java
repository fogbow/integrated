package cloud.fogbow.ms.api.http.request;

import cloud.fogbow.ms.core.PropertiesHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cloud.fogbow.common.constants.ApiDocumentation;
import cloud.fogbow.ms.api.http.response.VersionNumber;
import cloud.fogbow.ms.constants.ConfigurationPropertyDefaults;
import cloud.fogbow.ms.constants.ConfigurationPropertyKeys;
import cloud.fogbow.ms.constants.SystemConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@CrossOrigin
@RestController
@RequestMapping(value = Version.VERSION_ENDPOINT)
@Api(description = ApiDocumentation.Version.API)
public class Version {

    public static final String VERSION_ENDPOINT = SystemConstants.SERVICE_BASE_ENDPOINT + "version";

    @ApiOperation(value = ApiDocumentation.Version.GET_OPERATION)
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<VersionNumber> getVersion() {
        String versionNumber = getVersionNumber();
        return new ResponseEntity<VersionNumber>(new VersionNumber(versionNumber), HttpStatus.OK);
    }

    public String getVersionNumber() {
        return SystemConstants.API_VERSION_NUMBER + "-" + readBuildNumber();
    }

    private String readBuildNumber() {
        String build;
        build = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.BUILD_NUMBER_KEY,
                    ConfigurationPropertyDefaults.BUILD_NUMBER);
        return build;
    }
}
