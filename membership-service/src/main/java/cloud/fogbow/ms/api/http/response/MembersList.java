package cloud.fogbow.ms.api.http.response;

import cloud.fogbow.ms.constants.ApiDocumentation;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiResponse;

import java.util.List;

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
