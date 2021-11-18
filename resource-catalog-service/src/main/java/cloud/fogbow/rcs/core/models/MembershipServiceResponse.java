package cloud.fogbow.rcs.core.models;

import static cloud.fogbow.rcs.constants.SystemConstants.MEMBERS_KEY_JSON;

import com.google.gson.annotations.SerializedName;

import cloud.fogbow.common.util.GsonHolder;

public class MembershipServiceResponse {

    @SerializedName(MEMBERS_KEY_JSON)
    private String[] members;

    public String[] getMembers() {
        return members;
    }

    public static MembershipServiceResponse fromJson(String json) {
        return GsonHolder.getInstance().fromJson(json, MembershipServiceResponse.class);
    }
}
