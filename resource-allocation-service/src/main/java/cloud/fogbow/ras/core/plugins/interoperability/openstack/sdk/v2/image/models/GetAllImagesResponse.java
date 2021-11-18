package cloud.fogbow.ras.core.plugins.interoperability.openstack.sdk.v2.image.models;

import cloud.fogbow.common.util.GsonHolder;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import static cloud.fogbow.common.constants.OpenStackConstants.Image.IMAGES_KEY_JSON;
import static cloud.fogbow.common.constants.OpenStackConstants.Image.NEXT_KEY_JSON;

/**
 * Documentation: https://developer.openstack.org/api-ref/image/v2/
 * <p>
 * Response example:
 * {
 * "images": [
 * {
 * "status": "active",
 * "name": "cirros-0.3.2-x86_64-disk",
 * "container_format": "bare",
 * "disk_format": "qcow2",
 * "visibility": "public",
 * "min_disk": 0,
 * "id": "1bea47ed-f6a9-463b-b423-14b9cca9ad27",
 * "owner": "5ef70662f8b34079a6eddb8da9d75fe8",
 * "size": 13167616,
 * "min_ram": 0
 * }
 * ],
 * "next" : "/v2/images?marker=e663df3a-88a1-40f3-bc9a-ca96ab35a026"
 * }
 */
public class GetAllImagesResponse {
    @SerializedName(IMAGES_KEY_JSON)
    private List<GetImageResponse> images;
    @SerializedName(NEXT_KEY_JSON)
    private String next;

    public List<GetImageResponse> getImages() {
        return this.images;
    }

    public String getNext() {
        return this.next;
    }

    public static GetAllImagesResponse fromJson(String json) {
        return GsonHolder.getInstance().fromJson(json, GetAllImagesResponse.class);
    }
}
