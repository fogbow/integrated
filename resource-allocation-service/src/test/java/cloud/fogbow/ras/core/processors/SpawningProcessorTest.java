package cloud.fogbow.ras.core.processors;

import cloud.fogbow.common.exceptions.InternalServerErrorException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InstanceNotFoundException;
import cloud.fogbow.common.exceptions.UnavailableProviderException;
import cloud.fogbow.common.models.linkedlists.ChainedList;
import cloud.fogbow.ras.api.http.response.ComputeInstance;
import cloud.fogbow.ras.api.http.response.InstanceState;
import cloud.fogbow.ras.api.http.response.OrderInstance;
import cloud.fogbow.ras.constants.ConfigurationPropertyDefaults;
import cloud.fogbow.ras.core.BaseUnitTests;
import cloud.fogbow.ras.core.SharedOrderHolders;
import cloud.fogbow.ras.core.TestUtils;
import cloud.fogbow.ras.core.cloudconnector.CloudConnector;
import cloud.fogbow.ras.core.cloudconnector.CloudConnectorFactory;
import cloud.fogbow.ras.core.datastore.DatabaseManager;
import cloud.fogbow.ras.core.models.orders.AttachmentOrder;
import cloud.fogbow.ras.core.models.orders.Order;
import cloud.fogbow.ras.core.models.orders.OrderState;

@PrepareForTest({ CloudConnectorFactory.class, DatabaseManager.class })
public class SpawningProcessorTest extends BaseUnitTests {

    private static final int SPAWNING_SLEEP_TIME = 2000;

    private ChainedList<Order> failedOrderList;
    private ChainedList<Order> fulfilledOrderList;
    private ChainedList<Order> openOrderList;
    private ChainedList<Order> spawningOrderList;
    private ChainedList<Order> remoteOrderList;
    private CloudConnector cloudConnector;
    private SpawningProcessor processor;
    private Thread thread;
    
    @Rule
    public Timeout globalTimeout = new Timeout(100, TimeUnit.SECONDS);

    @Before
    public void setUp() throws InternalServerErrorException {
        this.testUtils.mockReadOrdersFromDataBase();
        this.testUtils.mockLocalCloudConnectorFromFactory();

        this.cloudConnector = CloudConnectorFactory.getInstance().getCloudConnector(TestUtils.LOCAL_MEMBER_ID,
                TestUtils.DEFAULT_CLOUD_NAME);

        this.processor = Mockito.spy(new SpawningProcessor(TestUtils.LOCAL_MEMBER_ID,
                ConfigurationPropertyDefaults.SPAWNING_ORDERS_SLEEP_TIME));

        SharedOrderHolders sharedOrderHolders = SharedOrderHolders.getInstance();
        this.spawningOrderList = sharedOrderHolders.getSpawningOrdersList();
        this.fulfilledOrderList = sharedOrderHolders.getFulfilledOrdersList();
        this.failedOrderList = sharedOrderHolders.getFailedAfterSuccessfulRequestOrdersList();
        this.openOrderList = sharedOrderHolders.getOpenOrdersList();
        this.remoteOrderList = sharedOrderHolders.getRemoteProviderOrdersList();

        this.thread = null;
    }

    @After
    public void tearDown() throws InternalServerErrorException {
        if (this.thread != null) {
            this.thread.interrupt();
        }
        super.tearDown();
    }

    // test case: In calling the processSpawningOrder() method for any order other than spawning,
    // you must not make state transition by keeping the order in your source list.
    @Test
    public void testProcessComputeOrderNotSpawning() throws Exception {
        // set up
        Order order = this.testUtils.createLocalOrder(this.testUtils.getLocalMemberId());
        order.setInstanceId(TestUtils.FAKE_INSTANCE_ID);
        order.setOrderState(OrderState.OPEN);
        this.openOrderList.addItem(order);

        // exercise
        this.processor.processSpawningOrder(order);

        // verify
        Assert.assertEquals(order, this.openOrderList.getNext());
        Assert.assertNull(this.fulfilledOrderList.getNext());
    }

    // test case: When running thread in the SpawningProcessor and the OrderType is not a
    // Compute, the processSpawningOrder() method must immediately change the OrderState to
    // Fulfilled by adding in that list, and removed from the Spawning list.
    @Test
    public void testRunProcessWhenOrderTypeIsNetwork() throws Exception {
        // set up
        Order order = this.testUtils.createLocalOrder(this.testUtils.getLocalMemberId());
        order.setInstanceId(TestUtils.FAKE_INSTANCE_ID);
        order.setOrderState(OrderState.SPAWNING);
        this.spawningOrderList.addItem(order);
        Assert.assertNull(this.fulfilledOrderList.getNext());

        OrderInstance orderInstance = new ComputeInstance(TestUtils.FAKE_INSTANCE_ID);
        orderInstance.setReady();

        Mockito.doReturn(orderInstance).when(this.cloudConnector)
                .getInstance(Mockito.any(Order.class));

        // exercise
        this.thread = new Thread(this.processor);
        this.thread.start();
        Thread.sleep(TestUtils.DEFAULT_SLEEP_TIME);

        // verify
        Order test = this.fulfilledOrderList.getNext();
        Assert.assertNotNull(test);
        Assert.assertEquals(order.getInstanceId(), test.getInstanceId());
        Assert.assertEquals(OrderState.FULFILLED, test.getOrderState());
        Assert.assertNull(this.spawningOrderList.getNext());
    }

    // test case: When running thread in the SpawningProcessor and the OrderType is not a
    // Compute, the processSpawningOrder() method must immediately change the OrderState to
    // Fulfilled by adding in that list, and removed from the Spawning list.
    @Test
    public void testRunProcessWhenOrderTypeIsVolume() throws Exception {
        // set up
        Order order = this.testUtils.createLocalVolumeOrder();
        order.setOrderState(OrderState.SPAWNING);
        this.spawningOrderList.addItem(order);
        Assert.assertNull(this.fulfilledOrderList.getNext());

        OrderInstance orderInstance = new ComputeInstance(TestUtils.FAKE_INSTANCE_ID);
        orderInstance.setReady();

        Mockito.doReturn(orderInstance).when(this.cloudConnector)
                .getInstance(Mockito.any(Order.class));

        // exercise
        this.thread = new Thread(this.processor);
        this.thread.start();
        Thread.sleep(TestUtils.DEFAULT_SLEEP_TIME);

        // verify
        Order test = this.fulfilledOrderList.getNext();
        Assert.assertNotNull(test);
        Assert.assertEquals(order.getInstanceId(), test.getInstanceId());
        Assert.assertEquals(OrderState.FULFILLED, test.getOrderState());
        Assert.assertNull(this.spawningOrderList.getNext());
    }

    // test case: When running thread in the SpawningProcessor and the OrderType is
    // not a Compute, the processSpawningOrder() method must immediately change the
    // OrderState to Fulfilled by adding in that list, and removed from the Spawning
    // list.
    @Test
    public void testRunProcessWhenOrderTypeIsAttachment() throws Exception {
        // set up
        AttachmentOrder attachmentOrder = this.testUtils.createLocalAttachmentOrder(
                this.testUtils.createLocalComputeOrder(), this.testUtils.createLocalVolumeOrder());
        attachmentOrder.setOrderState(OrderState.SPAWNING);
        this.spawningOrderList.addItem(attachmentOrder);
        Assert.assertNull(this.fulfilledOrderList.getNext());

        OrderInstance orderInstance = new ComputeInstance(TestUtils.FAKE_INSTANCE_ID);
        orderInstance.setReady();

        Mockito.doReturn(orderInstance).when(this.cloudConnector).getInstance(Mockito.any(Order.class));

        // exercise
        this.thread = new Thread(this.processor);
        this.thread.start();
        Thread.sleep(TestUtils.DEFAULT_SLEEP_TIME);

        // verify
        Order test = this.fulfilledOrderList.getNext();
        Assert.assertNotNull(test);
        Assert.assertEquals(attachmentOrder.getInstanceId(), test.getInstanceId());
        Assert.assertEquals(OrderState.FULFILLED, test.getOrderState());
        Assert.assertNull(this.spawningOrderList.getNext());
    }

    // test case: When running thread in the SpawningProcessor and the InstanceState is not
    // Ready, the method processSpawningOrder() must not change OrderState to Fulfilled and must
    // remain in Spawning list.
    @Test
    public void testRunProcessComputeOrderWhenInstanceStateIsNotReady() throws Exception {
        // set up
        Order order = this.testUtils.createLocalOrder(this.testUtils.getLocalMemberId());
        order.setInstanceId(TestUtils.FAKE_INSTANCE_ID);
        order.setOrderState(OrderState.SPAWNING);
        this.spawningOrderList.addItem(order);

        OrderInstance orderInstance = new ComputeInstance(TestUtils.FAKE_INSTANCE_ID);
        orderInstance.setState(InstanceState.DISPATCHED);

        Mockito.doReturn(orderInstance).when(this.cloudConnector)
                .getInstance(Mockito.any(Order.class));

        // exercise
        this.thread = new Thread(this.processor);
        this.thread.start();

        // verify
        Assert.assertEquals(order, this.spawningOrderList.getNext());
        Assert.assertNull(this.fulfilledOrderList.getNext());
    }

    // test case: When running thread in the SpawningProcessor and the InstanceState is Ready, the
    // processSpawningOrder() method must change the OrderState to Fulfilled by adding in that list,
    // and removed from the Spawning list.
    @Test
    public void testRunProcessComputeOrderInstanceReachable() throws Exception {
        // set up
        Order order = this.testUtils.createLocalOrder(this.testUtils.getLocalMemberId());
        order.setInstanceId(TestUtils.FAKE_INSTANCE_ID);
        order.setOrderState(OrderState.SPAWNING);
        this.spawningOrderList.addItem(order);
        Assert.assertNull(this.fulfilledOrderList.getNext());

        OrderInstance orderInstance = new ComputeInstance(TestUtils.FAKE_INSTANCE_ID);
        orderInstance.setReady();

        Mockito.doReturn(orderInstance).when(this.cloudConnector)
                .getInstance(Mockito.any(Order.class));

        // exercise
        this.thread = new Thread(this.processor);
        this.thread.start();
        Thread.sleep(SPAWNING_SLEEP_TIME);

        // verify
        Order test = this.fulfilledOrderList.getNext();
        Assert.assertNotNull(test);
        Assert.assertEquals(order.getInstanceId(), test.getInstanceId());
        Assert.assertEquals(OrderState.FULFILLED, test.getOrderState());
        Assert.assertNull(this.spawningOrderList.getNext());
    }

    // test case: When running thread in the SpawningProcessor and the InstanceState is Failed,
    // the processSpawningOrder() method must change the OrderState to Failed by adding in that
    // list, and removed from the Spawning list.
    @Test
    public void testRunProcessComputeOrderWhenInstanceStateIsFailed() throws Exception {
        // set up
        Order order = this.testUtils.createLocalOrder(this.testUtils.getLocalMemberId());
        order.setInstanceId(TestUtils.FAKE_INSTANCE_ID);
        order.setOrderState(OrderState.SPAWNING);
        this.spawningOrderList.addItem(order);
        Assert.assertNull(this.failedOrderList.getNext());

        OrderInstance orderInstance = new ComputeInstance(TestUtils.FAKE_INSTANCE_ID);
        orderInstance.setHasFailed();

        Mockito.doReturn(orderInstance).when(this.cloudConnector)
                .getInstance(Mockito.any(Order.class));

        // exercise
        this.thread = new Thread(this.processor);
        this.thread.start();
        Thread.sleep(TestUtils.DEFAULT_SLEEP_TIME);

        // verify
        Order test = this.failedOrderList.getNext();
        Assert.assertNotNull(test);
        Assert.assertEquals(order.getInstanceId(), test.getInstanceId());
        Assert.assertEquals(OrderState.FAILED_AFTER_SUCCESSFUL_REQUEST, test.getOrderState());
        Assert.assertNull(this.spawningOrderList.getNext());
    }
    
    // test case: During a thread running in SpawningProcessor, if any
    // errors occur when attempting to get a cloud provider instance, the
    // processSpawningOrder method will catch an exception.
    @Test
    public void testRunProcessLocalOrderToCatchExceptionWhileTryingToGetInstance()
            throws InterruptedException, FogbowException {

        // set up
        Order order = this.testUtils.createLocalOrder(this.testUtils.getLocalMemberId());
        order.setInstanceId(TestUtils.FAKE_INSTANCE_ID);
        order.setOrderState(OrderState.SPAWNING);
        this.spawningOrderList.addItem(order);

        Mockito.doThrow(new RuntimeException()).when(this.cloudConnector).getInstance(Mockito.any(Order.class));

        // exercise
        this.thread = new Thread(this.processor);
        this.thread.start();
        Thread.sleep(TestUtils.DEFAULT_SLEEP_TIME);

        // verify
        Mockito.verify(this.processor, Mockito.times(1)).processSpawningOrder(order);
    }
    
    // test case: Check the throw of InternalServerErrorException when running the thread in
    // the SpawningProcessor, while running a local order.
    @Test
    public void testRunProcessLocalOrderThrowsUnexpectedException() throws InterruptedException, FogbowException {
        // set up
        Order order = this.testUtils.createLocalOrder(this.testUtils.getLocalMemberId());
        order.setInstanceId(TestUtils.FAKE_INSTANCE_ID);
        order.setOrderState(OrderState.SPAWNING);
        this.spawningOrderList.addItem(order);

        Mockito.doThrow(new InternalServerErrorException()).when(this.processor).processSpawningOrder(order);

        // exercise
        this.thread = new Thread(this.processor);
        this.thread.start();
        Thread.sleep(TestUtils.DEFAULT_SLEEP_TIME);

        // verify
        Mockito.verify(this.processor, Mockito.times(1)).processSpawningOrder(order);
    }
    
    // test case: When invoking the processSpawningOrder method and an error occurs
    // while trying to get an instance from the cloud provider, an
    // UnavailableProviderException will be throw.
    @Test(expected = UnavailableProviderException.class) // Verify
    public void testProcessSpawningOrderThrowsUnavailableProviderException() throws FogbowException {
        // set up
        Order order = this.testUtils.createLocalOrder(this.testUtils.getLocalMemberId());
        order.setInstanceId(TestUtils.FAKE_INSTANCE_ID);
        order.setOrderState(OrderState.SPAWNING);
        this.spawningOrderList.addItem(order);

        Mockito.doThrow(new UnavailableProviderException()).when(this.cloudConnector)
                .getInstance(Mockito.any(Order.class));

        // exercise
        this.processor.processSpawningOrder(order);
    }
    
    // test case: When calling the processSpawningOrder method, if the order instance was not found
    // multiple times and there is only one attempt left it must change the order state to
    // FAILED_AFTER_SUCCESSFUL_REQUEST.
    @Test
    public void testProcessSpawningOrderWithInstanceNotFoundMultipleTimes() throws FogbowException {
        // set up
        Order order = this.testUtils.createLocalOrder(this.testUtils.getLocalMemberId());
        order.setInstanceId(TestUtils.FAKE_INSTANCE_ID);
        order.setOrderState(OrderState.SPAWNING);
        this.spawningOrderList.addItem(order);

        Mockito.doThrow(new InstanceNotFoundException()).when(this.cloudConnector)
                .getInstance(Mockito.any(Order.class));

        Map<Order, Integer> failedRequestsMap = new HashMap<>();
        int attemptsLeft = SpawningProcessor.FAILED_REQUESTS_LIMIT - 1;
        failedRequestsMap.put(order, attemptsLeft);
        this.processor.setFailedRequestsMap(failedRequestsMap);

        // exercise
        this.processor.processSpawningOrder(order);

        // verify
        Assert.assertEquals(OrderState.FAILED_AFTER_SUCCESSFUL_REQUEST, order.getOrderState());
    }
    
    // test case: When calling the processSpawningOrder method with a
    // remote member ID, the order state should change to PENDING.
    @Test
    public void testProcessSpawningOrderWithARemoteMember() throws FogbowException {
        // set up
        Order order = this.testUtils.createLocalOrder(this.testUtils.getLocalMemberId());
        order.setInstanceId(TestUtils.FAKE_INSTANCE_ID);
        order.setOrderState(OrderState.SPAWNING);
        this.spawningOrderList.addItem(order);

        this.processor = new SpawningProcessor(TestUtils.FAKE_REMOTE_MEMBER_ID,
                ConfigurationPropertyDefaults.UNABLE_TO_CHECK_ORDERS_SLEEP_TIME);

        // exercise
        this.processor.processSpawningOrder(order);

        // verify
        Assert.assertEquals(OrderState.PENDING, order.getOrderState());
        Assert.assertEquals(order, this.remoteOrderList.getNext());
        Assert.assertNull(this.fulfilledOrderList.getNext());
    }
    
    // test case: this method tests if, after starting a thread using a
    // SpawningProcessor instance, the method 'stop' stops correctly the thread
    @Test
    public void testStop() throws InterruptedException, FogbowException {
        this.thread = new Thread(this.processor);
        this.thread.start();
        
        while (!this.processor.isActive()) ;

        this.processor.stop();
        this.thread.join();
        
        Assert.assertFalse(this.thread.isAlive());
    }

}
