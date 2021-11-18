package cloud.fogbow.ras.core.plugins.interoperability.azure.util;

import cloud.fogbow.common.exceptions.UnacceptableOperationException;
import org.apache.log4j.Logger;

import rx.Completable;
import cloud.fogbow.ras.constants.Messages;

import com.microsoft.azure.management.Azure;
import com.microsoft.azure.management.resources.ResourceGroup;

public class AzureResourceGroupOperationUtil {

    private static final Logger LOGGER = Logger.getLogger(AzureResourceGroupOperationUtil.class);

    public static String createResourceGroup(Azure azure, String regionName, String resourceGroupName)
            throws UnacceptableOperationException {
        try {
            ResourceGroup resourceGroup = azure.resourceGroups()
                    .define(resourceGroupName)
                    .withRegion(regionName)
                    .create();
            
            return resourceGroup.name();
        } catch (RuntimeException e) {
            LOGGER.debug(String.format(Messages.Exception.GENERIC_EXCEPTION_S, e));
            throw new UnacceptableOperationException(Messages.Exception.RESOURCE_GROUP_LIMIT_EXCEEDED);
        }
    }

    public static boolean existsResourceGroup(Azure azure, String resourceGroupName) {
        return azure.resourceGroups().checkExistence(resourceGroupName);
    }

    public static Completable deleteResourceGroupAsync(Azure azure, String resourceGroupName) {
        return azure.resourceGroups().deleteByNameAsync(resourceGroupName);
    }

}
