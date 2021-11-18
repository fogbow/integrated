package cloud.fogbow.ras.core.datastore;

import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.models.linkedlists.SynchronizedDoublyLinkedList;
import cloud.fogbow.ras.core.datastore.services.AuditableOrderStateChangeService;
import cloud.fogbow.ras.core.datastore.services.AuditableRequestService;
import cloud.fogbow.ras.core.datastore.services.RecoveryService;
import cloud.fogbow.ras.core.models.auditing.AuditableRequest;
import cloud.fogbow.ras.core.models.orders.Order;
import cloud.fogbow.ras.core.models.orders.OrderState;
import org.apache.log4j.Logger;

public class DatabaseManager implements StableStorage {
    private static final Logger LOGGER = Logger.getLogger(DatabaseManager.class);

    private static DatabaseManager instance;

    private RecoveryService recoveryService;
    private AuditableOrderStateChangeService auditableOrderStateChangeService;
    private AuditableRequestService auditableRequestService;

    private DatabaseManager() {
    }

    public synchronized static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    @Override
    public void add(Order order) throws InternalServerErrorException {
        this.recoveryService.save(order);
        this.auditableOrderStateChangeService.registerStateChange(order);
    }

    @Override
    public void update(Order order, boolean orderStateChanged) throws InternalServerErrorException {
        this.recoveryService.update(order);
        if (orderStateChanged) {
            this.auditableOrderStateChangeService.registerStateChange(order);
        }
    }

    @Override
    public SynchronizedDoublyLinkedList<Order> readActiveOrders(OrderState orderState) throws InternalServerErrorException {

        SynchronizedDoublyLinkedList<Order> synchronizedDoublyLinkedList = new SynchronizedDoublyLinkedList<>();

        for (Order order : this.recoveryService.readActiveOrders(orderState)) {
            synchronizedDoublyLinkedList.addItem(order);
        }
        return synchronizedDoublyLinkedList;
    }

    public void update(Order order) throws InternalServerErrorException {
        update(order, true);
    }

    public void auditRequest(AuditableRequest request) throws InternalServerErrorException {
        this.auditableRequestService.registerSyncRequest(request);
    }

    public void setRecoveryService(RecoveryService recoveryService) {
        this.recoveryService = recoveryService;
    }

    public void setAuditableOrderStateChangeService(AuditableOrderStateChangeService auditableOrderStateChangeService) {
        this.auditableOrderStateChangeService = auditableOrderStateChangeService;
    }

    public void setAuditableRequestService(AuditableRequestService auditableRequestService) {
        this.auditableRequestService = auditableRequestService;
    }
}
