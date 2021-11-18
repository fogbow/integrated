package cloud.fogbow.rcs.api.http.response;

import java.util.List;

import cloud.fogbow.rcs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModelProperty;

public class MembersList {
    
    @ApiModelProperty(example = ApiDocumentation.Model.MEMBERS_LIST)
    private List<String> members;

    public MembersList() {}

    public MembersList(List<String> members) {
        this.members = members;
    }

    public List<String> getMembers() {
        return members;
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }
}
