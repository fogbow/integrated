package cloud.fogbow.rcs.api.http.request;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.rcs.constants.*;
import cloud.fogbow.rcs.core.ApplicationFacade;
import cloud.fogbow.rcs.core.PropertiesHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@CrossOrigin
@Controller
@RequestMapping(value = Service.ENDPOINT)
@Api(description = ApiDocumentation.Catalog.API)
public class Service {

    protected static final String ENDPOINT = SystemConstants.SERVICE_BASE_ENDPOINT + "services";
    protected static final String CATALOG_VIEW = PropertiesHolder.getInstance().getProperty(
            ConfigurationPropertyKeys.SERVICE_CATALOG_VIEW, ConfigurationPropertyDefaults.DEFAULT_CATALOG_VIEW);
    protected static final String CATALOG_SPEC_ATTRIBUTE = PropertiesHolder.getInstance().getProperty(
            ConfigurationPropertyKeys.SERVICE_CATALOG_SPEC, ConfigurationPropertyDefaults.DEFAULT_SERVICE_CATALOG_SPEC_ATTRIBUTE);

    private static final Logger LOGGER = Logger.getLogger(Service.class);

    @ApiOperation(value = ApiDocumentation.Catalog.GET_SERVICE_OPERATION)
    @RequestMapping(value = "/{member:.+}/{service}", method = RequestMethod.GET)
    public String getService(
            @ApiParam(value = ApiDocumentation.Catalog.MEMBER)
            @PathVariable String member,
            @ApiParam(value = ApiDocumentation.Catalog.SERVICE)
            @PathVariable String service,
            Model model) throws FogbowException {
        try {
            LOGGER.info(String.format(Messages.Info.GETTING_SERVICE));

            String catalogSpec = ApplicationFacade.getInstance().getService(member, service);
            model.addAttribute(CATALOG_SPEC_ATTRIBUTE, catalogSpec);

            return CATALOG_VIEW;
        } catch (Exception e) {
            LOGGER.error(String.format(Messages.Exception.GENERIC_EXCEPTION, e.getMessage()), e);
            throw e;
        }
    }

    @ApiOperation(value = ApiDocumentation.Catalog.DELETE_SERVICE_OPERATION)
    @RequestMapping(value = "/{member:.+}/{service}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteService(
            @ApiParam(value = ApiDocumentation.Catalog.MEMBER)
            @PathVariable String member,
            @ApiParam(value = ApiDocumentation.Catalog.SERVICE)
            @PathVariable String service) {
        try {
            LOGGER.info(String.format(Messages.Info.DELETING_SERVICE));
            ApplicationFacade.getInstance().removeCache(member, service);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(String.format(Messages.Exception.GENERIC_EXCEPTION, e.getMessage()), e);
            throw e;
        }
    }
}
