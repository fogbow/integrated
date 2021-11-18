package cloud.fogbow.ras.core.plugins.interoperability.azure.sdk.image;

import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.ras.api.http.response.ImageSummary;
import cloud.fogbow.ras.constants.Messages;
import cloud.fogbow.ras.core.plugins.interoperability.azure.util.AzureImageOperationUtil;
import com.microsoft.azure.PagedList;
import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.compute.VirtualMachineOffer;
import com.microsoft.azure.management.compute.VirtualMachinePublisher;
import com.microsoft.azure.management.compute.VirtualMachineSku;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AzureImageOperation {
    private final String region;
    private static final Logger LOGGER = Logger.getLogger(AzureImageOperation.class);

    public AzureImageOperation(String region) {
        this.region = region;
    }

    public PagedList<VirtualMachineSku> getSkusFrom(VirtualMachineOffer offer) {
        return offer.skus().list();
    }

    public PagedList<VirtualMachineOffer> getOffersFrom(VirtualMachinePublisher publisher) {
        return publisher.offers().list();
    }

    public PagedList<VirtualMachinePublisher> getPublishers(Azure azure) {
        return azure.virtualMachineImages().publishers().listByRegion(this.region);
    }

    public Map<String, ImageSummary> getImages(Azure azure, List<String> publishers) throws InternalServerErrorException {
        Map<String, ImageSummary> images = new HashMap<>();

        for (VirtualMachinePublisher publisher : this.getPublishers(azure)) {
            if (publishers.contains(publisher.name())) {
                for (VirtualMachineOffer offer : this.getOffersFrom(publisher)) {
                    for (VirtualMachineSku sku : this.getSkusFrom(offer)) {
                        String publisherName = publisher.name();
                        String offerName = offer.name();
                        String skuName = sku.name();
                        ImageSummary imageSummary = AzureImageOperationUtil.buildImageSummaryBy(publisherName, offerName, skuName);
                        String id;

                        try {
                            id = AzureImageOperationUtil.encode(imageSummary.getId());
                        } catch (InternalServerErrorException ex) {
                            LOGGER.debug(String.format(Messages.Log.ERROR_WHILE_LOADING_IMAGE_S, imageSummary.getName()));
                            continue;
                        }

                        images.put(id, imageSummary);
                    }
                }
            }
        }

        return images;
    }
}
