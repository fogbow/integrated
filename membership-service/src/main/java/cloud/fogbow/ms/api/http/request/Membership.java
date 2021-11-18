package cloud.fogbow.ms.api.http.request;

import cloud.fogbow.ms.constants.SystemConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.log4j.Logger;

import cloud.fogbow.ms.core.ApplicationFacade;
import cloud.fogbow.ms.constants.ApiDocumentation;
import cloud.fogbow.ms.constants.Messages;
import cloud.fogbow.ms.api.http.response.MembersList;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = Membership.ENDPOINT)
@Api(description = ApiDocumentation.Membership.API)
public class Membership {

    protected static final String ENDPOINT = SystemConstants.SERVICE_BASE_ENDPOINT + "members";

    private static final Logger LOGGER = Logger.getLogger(Membership.class);

    public Membership() {

    }

    /**
     * Gets JSON response with a list of XMPP members ID.
     */
    @ApiOperation(value = ApiDocumentation.Membership.DESCRIPTION)
    @GetMapping
    public ResponseEntity<MembersList> listMembers() {
        try {
            List<String> membersId = ApplicationFacade.getInstance().listMembers();
            MembersList membersList = new MembersList(membersId);
            return new ResponseEntity<MembersList>(membersList, HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(Messages.Log.INTERNAL_SERVER_ERROR, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
