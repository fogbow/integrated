package cloud.fogbow.rcs.api.http.response;

import io.swagger.annotations.ApiModelProperty;

public class VersionNumber {
    @ApiModelProperty(example = "v.1.0.0-rcs-3191466-ras-ec6bf564-as-c803775-common-4e0d74e")
    private String version;

    public VersionNumber() {}

    public VersionNumber(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
