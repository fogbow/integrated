package cloud.fogbow.rcs;

import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import cloud.fogbow.rcs.constants.Messages;
import cloud.fogbow.rcs.core.ApplicationFacade;
import cloud.fogbow.rcs.core.intercomponent.RemoteFacade;
import cloud.fogbow.rcs.core.intercomponent.xmpp.PacketSenderHolder;
import cloud.fogbow.rcs.core.service.CatalogService;

@Component
public class Main implements ApplicationRunner {

    private static final Logger LOGGER = Logger.getLogger(Main.class);
    
    @Override
    public void run(ApplicationArguments args) {
        // Instantiating CatalogService
        CatalogService catalogService = new CatalogService();

        // Instantiating Facades
        ApplicationFacade applicationFacade = ApplicationFacade.getInstance();
        applicationFacade.setCatalogService(catalogService);

        RemoteFacade remoteFacade = RemoteFacade.getInstance();
        remoteFacade.setCatalogService(catalogService);

        // Starting PacketSender
        while (true) {
            try {
                PacketSenderHolder.init();
                break;
            } catch (IllegalStateException e) {
                LOGGER.error(Messages.Error.NO_PACKET_SENDER, e);
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException ex) {
                    LOGGER.error("", ex);
                }
            }
        }
    }

}