package cloud.fogbow.rcs.api.http.request;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.rcs.api.http.response.MembersList;
import cloud.fogbow.rcs.api.http.response.ServicesList;
import cloud.fogbow.rcs.constants.ApiDocumentation;
import cloud.fogbow.rcs.constants.Messages;
import cloud.fogbow.rcs.constants.SystemConstants;
import cloud.fogbow.rcs.core.ApplicationFacade;
import cloud.fogbow.rcs.core.models.Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@CrossOrigin
@RestController
@RequestMapping(value = Catalog.ENDPOINT)
@Api(ApiDocumentation.Catalog.API)
public class Catalog {

    protected static final String ENDPOINT = SystemConstants.SERVICE_BASE_ENDPOINT + "members";

    private static final Logger LOGGER = Logger.getLogger(Catalog.class);

    @ApiOperation(value = ApiDocumentation.Catalog.GET_ALL_MEMBERS_OPERATION)
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<MembersList> getAllMembers() throws FogbowException {
        try {
            LOGGER.info(String.format(Messages.Info.GETTING_ALL_MEMBERS));
            List<String> members = ApplicationFacade.getInstance().getMembers();
            return new ResponseEntity<>(new MembersList(members), HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(String.format(Messages.Exception.GENERIC_EXCEPTION, e.getMessage()), e);
            throw e;
        }
    }
    
    @ApiOperation(value = ApiDocumentation.Catalog.GET_ALL_SERVICES_BY_MEMBER_OPERATION)
    @RequestMapping(value = "/{member}", method = RequestMethod.GET)
    public ResponseEntity<ServicesList> getAllServices(
            @ApiParam(value = ApiDocumentation.Catalog.MEMBER) 
            @PathVariable String member) throws FogbowException {
        try {
            LOGGER.info(String.format(Messages.Info.GETTING_ALL_SERVICES));
            List<Service> services = ApplicationFacade.getInstance().getServicesFrom(member);
            return new ResponseEntity<>(new ServicesList(services), HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(String.format(Messages.Exception.GENERIC_EXCEPTION, e.getMessage()), e);
            throw e;
        }
    }

}
