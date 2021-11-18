package cloud.fogbow.ras.core.plugins.interoperability.opennebula.sdk.v5_4.volume.model;

public class CreateVolumeRequest {

    private VolumeImage volumeImage;

    public CreateVolumeRequest(Builder builder) {
    	this.volumeImage = new VolumeImage();
    	this.volumeImage.setName(builder.name);
    	this.volumeImage.setImagePersistent(builder.imagePersistent);
    	this.volumeImage.setImageType(builder.imageType);
    	this.volumeImage.setDriver(builder.driver);
    	this.volumeImage.setDiskType(builder.diskType);
    	this.volumeImage.setDevicePrefix(builder.devicePrefix);
    	this.volumeImage.setSize(builder.size);
    }

    public VolumeImage getVolumeImage() {
        return volumeImage;
    }

    public static class Builder {
        
    	private String name;
        private String imagePersistent;
        private String imageType;
        private String driver;
        private String diskType;
        private String devicePrefix;
        private long size;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder imagePersistent(String imagePersistent) {
            this.imagePersistent = imagePersistent;
            return this;
        }

        public Builder imageType(String imageType) {
            this.imageType = imageType;
            return this;
        }

        public Builder driver(String driver) {
            this.driver = driver;
            return this;
        }

        public Builder diskType(String diskType) {
            this.diskType = diskType;
            return this;
        }

        public Builder devicePrefix(String devicePrefix) {
            this.devicePrefix = devicePrefix;
            return this;
        }

        public Builder size(long size) {
            this.size = size;
            return this;
        }

        public CreateVolumeRequest build(){
            return new CreateVolumeRequest(this);
        }
    }
}
