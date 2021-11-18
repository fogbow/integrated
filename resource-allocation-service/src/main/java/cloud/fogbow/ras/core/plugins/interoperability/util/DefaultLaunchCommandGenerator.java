package cloud.fogbow.ras.core.plugins.interoperability.util;

import cloud.fogbow.common.exceptions.FatalErrorException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.util.CloudInitUserDataBuilder;
import cloud.fogbow.ras.constants.ConfigurationPropertyDefaults;
import cloud.fogbow.ras.constants.ConfigurationPropertyKeys;
import cloud.fogbow.ras.constants.Messages;
import cloud.fogbow.ras.core.PropertiesHolder;
import cloud.fogbow.ras.core.models.UserData;
import cloud.fogbow.ras.core.models.orders.ComputeOrder;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultLaunchCommandGenerator implements LaunchCommandGenerator {
    private static final Logger LOGGER = Logger.getLogger(DefaultLaunchCommandGenerator.class);

    protected static final String TOKEN_ID = "#TOKEN_ID#";
    protected static final String TOKEN_SSH_USER = "#TOKEN_SSH_USER#";
    protected static final String TOKEN_USER_SSH_PUBLIC_KEY = "#TOKEN_USER_SSH_PUBLIC_KEY#";
    public static final String USER_DATA_LINE_BREAKER = "[[\\n]]";
    private final String BRING_UP_NETWORK_INTERFACE_SCRIPT_PATH = "bin/bring-up-network-interface";
    private final String CLOUD_CONFIG_FILE_PATH = "bin/cloud-config.cfg";
    private final String sshCommonUser;

    public DefaultLaunchCommandGenerator() {
        this.sshCommonUser = PropertiesHolder.getInstance().getProperty(ConfigurationPropertyKeys.SSH_COMMON_USER_KEY,
                ConfigurationPropertyDefaults.SSH_COMMON_USER);
    }

    @Override
    public String createLaunchCommand(ComputeOrder order) throws InternalServerErrorException {
        CloudInitUserDataBuilder cloudInitUserDataBuilder = CloudInitUserDataBuilder.start();
        try {
            // Here, we need to instantiate the FileReader, because, once we read this file, the stream goes to the end
            // of the file, preventing to read the file again.
            cloudInitUserDataBuilder.addCloudConfig(new FileReader(this.CLOUD_CONFIG_FILE_PATH));
            if (order.getNetworkIds().size() > 0) {
                cloudInitUserDataBuilder.addShellScript(new FileReader(this.BRING_UP_NETWORK_INTERFACE_SCRIPT_PATH));
            }
        } catch (IOException e) {
            throw new FatalErrorException(e.getMessage());
        }

        List<UserData> userDataScripts = order.getUserData();

        if (userDataScripts != null) {
            for (UserData userDataScript : userDataScripts) {
                if (userDataScript != null) {
                    String normalizedExtraUserData = null;
                    String extraUserDataFileContent = userDataScript.getExtraUserDataFileContent();
                    if (extraUserDataFileContent != null) {
                        normalizedExtraUserData = new String(Base64.decodeBase64(extraUserDataFileContent));
                    }

                    CloudInitUserDataBuilder.FileType extraUserDataFileType = userDataScript.getExtraUserDataFileType();
                    addExtraUserData(cloudInitUserDataBuilder, normalizedExtraUserData, extraUserDataFileType);
                }
            }
        }


        String mimeString = cloudInitUserDataBuilder.buildUserData();
        mimeString = applyTokensReplacements(order, mimeString);
        String base64String = new String(Base64.encodeBase64(mimeString.getBytes(StandardCharsets.UTF_8),
                false, false), StandardCharsets.UTF_8);
        return base64String;
    }

    protected void addExtraUserData(CloudInitUserDataBuilder cloudInitUserDataBuilder, String extraUserDataFileContent,
                                    CloudInitUserDataBuilder.FileType extraUserDataFileType)
            throws InternalServerErrorException {

        if (extraUserDataFileContent != null && extraUserDataFileType != null) {
            String lineSeparator = "\n";
            String normalizedExtraUserData = extraUserDataFileContent.replace(USER_DATA_LINE_BREAKER, lineSeparator);

            cloudInitUserDataBuilder.addFile(extraUserDataFileType, new StringReader(normalizedExtraUserData));
        } else if (extraUserDataFileContent == null) {
            LOGGER.warn(Messages.Log.UNABLE_TO_ADD_EXTRA_USER_DATA_FILE_CONTENT_NULL);
        } else {
            LOGGER.warn(Messages.Log.UNABLE_TO_ADD_EXTRA_USER_DATA_FILE_TYPE_NULL);
        }
    }

    protected String applyTokensReplacements(ComputeOrder order, String mimeString) {
        String orderId = order.getId();

        Map<String, String> replacements = new HashMap<String, String>();
        replacements.put(TOKEN_ID, orderId);

        String userPublicKey = order.getPublicKey();
        if (userPublicKey == null) {
            userPublicKey = "";
        }

        replacements.put(TOKEN_USER_SSH_PUBLIC_KEY, userPublicKey);
        replacements.put(TOKEN_SSH_USER, this.sshCommonUser);

        for (String key : replacements.keySet()) {
            String value = replacements.get(key);
            mimeString = mimeString.replace(key, value);
        }
        return mimeString;
    }
}
