package cloud.fogbow.rcs.api.http.parameters;

import cloud.fogbow.rcs.constants.ApiDocumentation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class ExpirationTimeProperty {
    private static final int FIRST_ATTR_POSITION = 0;

    @ApiModelProperty(position = FIRST_ATTR_POSITION, example = ApiDocumentation.Model.EXPIRATION_TIME, required = true)
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
