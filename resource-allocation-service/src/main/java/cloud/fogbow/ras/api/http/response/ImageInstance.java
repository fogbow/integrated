package cloud.fogbow.ras.api.http.response;

import cloud.fogbow.ras.constants.ApiDocumentation;
import io.swagger.annotations.ApiModelProperty;

public class ImageInstance extends Instance {
    @ApiModelProperty(position = 3, example = ApiDocumentation.Model.IMAGE_NAME)
    private String name;
    @ApiModelProperty(position = 4, example = "1.47 GB")
    private long size; // in bytes
    @ApiModelProperty(position = 5, example = "2")
    private long minDisk; // in GB
    @ApiModelProperty(position = 6, example = "0")
    private long minRam; // in MB
    @ApiModelProperty(position = 7, example = "Active")
    private String status;

    public ImageInstance(String id, String name, long size, long minDisk, long minRam, String status) {
        super(id);
        this.name = name;
        this.size = size;
        this.minDisk = minDisk;
        this.minRam = minRam;
        this.status = status;
    }

    public String getName() {
        return this.name;
    }

    public String getStatus() {
        return status;
    }

    public long getSize() {
        return size;
    }

    public long getMinDisk() {
        return minDisk;
    }

    public long getMinRam() {
        return minRam;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (minDisk ^ (minDisk >>> 32));
        result = prime * result + (int) (minRam ^ (minRam >>> 32));
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + (int) (size ^ (size >>> 32));
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ImageInstance other = (ImageInstance) obj;
        if (super.getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!super.getId().equals(other.getId()))
            return false;
        if (minDisk != other.minDisk)
            return false;
        if (minRam != other.minRam)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (size != other.size)
            return false;
        if (status == null) {
            if (other.status != null)
                return false;
        } else if (!status.equals(other.status))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "ImageInstance{" +
                "id='" + getId() + '\'' +
                ", name='" + name + '\'' +
                ", size=" + size +
                ", minDisk=" + minDisk +
                ", minRam=" + minRam +
                ", status='" + status + '\'' +
                '}';
    }
}
