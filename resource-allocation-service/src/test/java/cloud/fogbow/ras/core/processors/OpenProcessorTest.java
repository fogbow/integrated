package cloud.fogbow.ras.core.processors;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;

import cloud.fogbow.common.exceptions.FogbowException;
import cloud.fogbow.common.exceptions.InternalServerErrorException;
import cloud.fogbow.common.models.linkedlists.ChainedList;
import cloud.fogbow.ras.constants.ConfigurationPropertyDefaults;
import cloud.fogbow.ras.core.BaseUnitTests;
import cloud.fogbow.ras.core.OrderController;
import cloud.fogbow.ras.core.SharedOrderHolders;
import cloud.fogbow.ras.core.TestUtils;
import cloud.fogbow.ras.core.cloudconnector.CloudConnector;
import cloud.fogbow.ras.core.cloudconnector.CloudConnectorFactory;
import cloud.fogbow.ras.core.datastore.DatabaseManager;
import cloud.fogbow.ras.core.models.orders.Order;
import cloud.fogbow.ras.core.models.orders.OrderState;

@PrepareForTest({ CloudConnectorFactory.class, DatabaseManager.class })
public class OpenProcessorTest extends BaseUnitTests {

    private static final int OPEN_SLEEP_TIME = 1000;
    
    private CloudConnector cloudConnector;
    private OrderController orderController;
    private OpenProcessor processor;
    private Thread thread;
    
    @Rule
    public Timeout globalTimeout = new Timeout(100, TimeUnit.SECONDS);

    @Before
    public void setUp() throws InternalServerErrorException {
        this.testUtils.mockReadOrdersFromDataBase();
        this.testUtils.mockLocalCloudConnectorFromFactory();

        this.cloudConnector = CloudConnectorFactory.getInstance().getCloudConnector(TestUtils.LOCAL_MEMBER_ID,
                TestUtils.DEFAULT_CLOUD_NAME);

        this.processor = Mockito.spy(new OpenProcessor(TestUtils.LOCAL_MEMBER_ID, 
                ConfigurationPropertyDefaults.OPEN_ORDERS_SLEEP_TIME));

        this.orderController = new OrderController();
        this.thread = null;
    }

    @After
    public void tearDown() throws InternalServerErrorException {
        if (this.thread != null) {
            this.thread.interrupt();
        }
        super.tearDown();
    }

    //test case: test if the open processor is setting to spawning an open order when the request
    //instance method of instance provider returns an instance.
    @Test
    public void testProcessOpenLocalOrder() throws Exception {
        //set up
        Order localOrder = this.testUtils.createLocalOrder(this.testUtils.getLocalMemberId());

        this.orderController.activateOrder(localOrder);

        Mockito.doReturn(TestUtils.FAKE_INSTANCE_ID)
                .when(this.cloudConnector)
                .requestInstance(Mockito.any(Order.class));

        //exercise
        this.thread = new Thread(this.processor);
        this.thread.start();

        Thread.sleep(OPEN_SLEEP_TIME);

        //verify
        Assert.assertEquals(OrderState.SPAWNING, localOrder.getOrderState());

        // test if the open order list is empty and 
        // the spawningList is with the localOrder
        SharedOrderHolders sharedOrderHolders = SharedOrderHolders.getInstance();
        ChainedList<Order> openOrdersList = sharedOrderHolders.getOpenOrdersList();
        ChainedList<Order> spawningOrdersList = sharedOrderHolders.getSpawningOrdersList();
        Assert.assertTrue(this.listIsEmpty(openOrdersList));
        Assert.assertSame(localOrder, spawningOrdersList.getNext());
    }

    //test case: test if the open processor is setting to failed an open order when the request instance
    //method of instance provider returns a null instance.
    @Test
    public void testProcessOpenLocalOrderWithNullInstance() throws Exception {
        //set up
        Order localOrder = this.testUtils.createLocalOrder(this.testUtils.getLocalMemberId());

        this.orderController.activateOrder(localOrder);

        Mockito.doReturn(null)
                .when(this.cloudConnector)
                .requestInstance(Mockito.any(Order.class));

        //exercise
        this.thread = new Thread(this.processor);
        this.thread.start();
        Thread.sleep(TestUtils.DEFAULT_SLEEP_TIME);

        //verify
        Assert.assertEquals(OrderState.FAILED_ON_REQUEST, localOrder.getOrderState());

        // test if the open order list is empty and the failedList is with the
        // localOrder
        SharedOrderHolders sharedOrderHolders = SharedOrderHolders.getInstance();
        ChainedList<Order> openOrdersList = sharedOrderHolders.getOpenOrdersList();
        ChainedList<Order> failedOrdersList = sharedOrderHolders.getFailedOnRequestOrdersList();
        Assert.assertTrue(this.listIsEmpty(openOrdersList));
        Assert.assertSame(localOrder, failedOrdersList.getNext());
    }

    //test case: test if the open processor is setting to failed an open order when the request instance
    //method of instance provider throws an exception.
    @Test
    public void testProcessLocalOpenOrderRequestingException() throws Exception {
        //set up
        Order localOrder = this.testUtils.createLocalOrder(this.testUtils.getLocalMemberId());

        this.orderController.activateOrder(localOrder);

        Mockito.doThrow(new RuntimeException())
                .when(this.cloudConnector)
                .requestInstance(Mockito.any(Order.class));

        //exercise
        this.thread = new Thread(this.processor);
        this.thread.start();
        Thread.sleep(TestUtils.DEFAULT_SLEEP_TIME);


        //verify
        Assert.assertEquals(OrderState.FAILED_ON_REQUEST, localOrder.getOrderState());

        // test if the open order list is empty and 
        // the failedList is with the localOrder
        SharedOrderHolders sharedOrderHolders = SharedOrderHolders.getInstance();
        ChainedList<Order> openOrdersList = sharedOrderHolders.getOpenOrdersList();
        ChainedList<Order> failedOrdersList = sharedOrderHolders.getFailedOnRequestOrdersList();
        Assert.assertTrue(this.listIsEmpty(openOrdersList));
        Assert.assertSame(localOrder, failedOrdersList.getNext());
    }

    //test case: test if the open processor is setting to pending an open intercomponent order.
    @Test
    public void testProcessOpenRemoteOrder() throws Exception {
        //set up
        Order remoteOrder = this.testUtils.createRemoteOrder(this.testUtils.getLocalMemberId());

        this.orderController.activateOrder(remoteOrder);

        Mockito.doReturn(null)
                .when(this.cloudConnector)
                .requestInstance(Mockito.any(Order.class));

        //exercise
        this.thread = new Thread(this.processor);
        this.thread.start();
        Thread.sleep(TestUtils.DEFAULT_SLEEP_TIME);

        //verify
        Assert.assertEquals(OrderState.PENDING, remoteOrder.getOrderState());

        // test if the open order list is empty and
        // the pendingList is with the localOrder
        SharedOrderHolders sharedOrderHolders = SharedOrderHolders.getInstance();
        ChainedList<Order> openOrdersList = sharedOrderHolders.getOpenOrdersList();
        ChainedList<Order> pendingOrdersList = sharedOrderHolders.getRemoteProviderOrdersList();
        Assert.assertTrue(this.listIsEmpty(openOrdersList));
        Assert.assertSame(remoteOrder, pendingOrdersList.getNext());
    }

    // test case: test if the open processor is setting to FAIL_ON_REQUEST an open intercomponent order when the
    // request instance method of intercomponent instance provider throws an exception.
    @Test
    public void testProcessRemoteOpenOrderRequestingException() throws Exception {
        //set up
        Order remoteOrder = this.testUtils.createRemoteOrder(this.testUtils.getLocalMemberId());

        this.orderController.activateOrder(remoteOrder);

        Mockito.doThrow(new RuntimeException("Any Exception"))
                .when(this.cloudConnector)
                .requestInstance(Mockito.any(Order.class));

        //exercise
        this.thread = new Thread(this.processor);
        this.thread.start();
        Thread.sleep(TestUtils.DEFAULT_SLEEP_TIME);

        //verify
        Assert.assertEquals(OrderState.FAILED_ON_REQUEST, remoteOrder.getOrderState());

        // test if the open order list is empty and
        // the remoteOrdersList is with the localOrder
        SharedOrderHolders sharedOrderHolders = SharedOrderHolders.getInstance();
        ChainedList<Order> openOrdersList = sharedOrderHolders.getOpenOrdersList();
        ChainedList<Order> remoteOrdersList = sharedOrderHolders.getRemoteProviderOrdersList();
        Assert.assertTrue(this.listIsEmpty(openOrdersList));
        Assert.assertSame(remoteOrder, remoteOrdersList.getNext());
    }

    //test case: test if the open processor does not process an Order that is not in the open state.
    @Test
    public void testProcessNotOpenOrder() throws InterruptedException, FogbowException {
        //set up
        Order order = this.testUtils.createLocalOrder(this.testUtils.getLocalMemberId());

        this.orderController.activateOrder(order);

        order.setOrderState(OrderState.PENDING);

        //exercise
        this.thread = new Thread(this.processor);
        this.thread.start();
        Thread.sleep(TestUtils.DEFAULT_SLEEP_TIME);

        //verify
        SharedOrderHolders sharedOrderHolders = SharedOrderHolders.getInstance();
        ChainedList<Order> openOrdersList = sharedOrderHolders.getOpenOrdersList();
        Assert.assertEquals(OrderState.PENDING, order.getOrderState());
        Assert.assertFalse(this.listIsEmpty(openOrdersList));
    }

    //test case: test if the open processor still running if it try to process a null order.
    @Test
    public void testRunProcessWithNullOpenOrder() throws InterruptedException {
        //verify
        this.thread = new Thread(this.processor);
        this.thread.start();
        Thread.sleep(TestUtils.DEFAULT_SLEEP_TIME);
    }

    // test case: test if the open processor changes and keep the order state as SELECTED
    // if the method processOpenOrder throws an Error or the system shutdown.
    @Test
    public void testProcessLocalOpenOrderAndTheSystemShutdown() throws Exception {
        //set up
        Order localOrder = this.testUtils.createLocalOrder(this.testUtils.getLocalMemberId());
        this.orderController.activateOrder(localOrder);

        // Simulate a system shutdown
        Mockito.doThrow(new Error())
                .when(this.cloudConnector)
                .requestInstance(Mockito.any(Order.class));

        //exercise
        this.thread = new Thread(this.processor);
        this.thread.start();
        Thread.sleep(TestUtils.DEFAULT_SLEEP_TIME);

        //verify
        Assert.assertEquals(OrderState.SELECTED, localOrder.getOrderState());

        // test if the open order list is empty and
        // the selectedList is with the localOrder
        SharedOrderHolders sharedOrderHolders = SharedOrderHolders.getInstance();
        ChainedList<Order> openOrdersList = sharedOrderHolders.getOpenOrdersList();
        ChainedList<Order> selectedOrdersList = sharedOrderHolders.getSelectedOrdersList();
        Assert.assertTrue(this.listIsEmpty(openOrdersList));
        Assert.assertSame(localOrder, selectedOrdersList.getNext());
    }

    //test case: test if the open processor still run and do not change the order state if the method
    //processOpenOrder throws an exception.
    @Test
    public void testProcessOpenOrderThrowingAnException() throws Exception {
        //set up
        Order order = this.testUtils.createLocalOrder(this.testUtils.getLocalMemberId());

        this.orderController.activateOrder(order);

        Mockito.doThrow(Exception.class)
                .when(this.processor)
                .processOpenOrder(Mockito.any(Order.class));

        //exercise
        this.thread = new Thread(this.processor);
        this.thread.start();
        Thread.sleep(TestUtils.DEFAULT_SLEEP_TIME);

        //verify
        SharedOrderHolders sharedOrderHolders = SharedOrderHolders.getInstance();
        ChainedList<Order> openOrdersList = sharedOrderHolders.getOpenOrdersList();
        openOrdersList.addItem(order);
        Assert.assertEquals(OrderState.OPEN, order.getOrderState());
        Assert.assertFalse(this.listIsEmpty(openOrdersList));
    }

    //test case: this method tests a race condition when this class thread has the order operation priority.
    @Test
    public void testRaceConditionWithThisThreadPriority() throws Exception {
        //set up
        Order localOrder = this.testUtils.createLocalOrder(this.testUtils.getLocalMemberId());

        this.orderController.activateOrder(localOrder);

        Mockito.doReturn(TestUtils.FAKE_INSTANCE_ID)
                .when(this.cloudConnector)
                .requestInstance(Mockito.any(Order.class));

        //exercise
        synchronized (localOrder) {
            this.thread = new Thread(this.processor);
            this.thread.start();
            Thread.sleep(TestUtils.DEFAULT_SLEEP_TIME);

            Assert.assertEquals(OrderState.OPEN, localOrder.getOrderState());
        }

        Thread.sleep(TestUtils.DEFAULT_SLEEP_TIME);

        //verify
        Assert.assertEquals(OrderState.SPAWNING, localOrder.getOrderState());
    }

    //test case: this method tests a race condition when this class thread has the order operation priority
    //and changes the open order to a different state.
    @Test
    public void testRaceConditionWithThisThreadPriorityAndNotOpenOrder()
            throws Exception {
        //set up
        Order localOrder = this.testUtils.createLocalOrder(this.testUtils.getLocalMemberId());

        this.orderController.activateOrder(localOrder);

        //exercise
        synchronized (localOrder) {
            this.thread = new Thread(this.processor);
            this.thread.start();
            Thread.sleep(TestUtils.DEFAULT_SLEEP_TIME);

            localOrder.setOrderState(OrderState.CHECKING_DELETION);
        }

        Thread.sleep(TestUtils.DEFAULT_SLEEP_TIME);

        //verify
        Assert.assertEquals(OrderState.CHECKING_DELETION, localOrder.getOrderState());
    }

    //test case: this method tests a race condition when the attend open order thread has the order operation priority.
    @Test
    public void testRaceConditionWithOpenProcessorThreadPriority() throws Exception {
        //set up
        Order localOrder = this.testUtils.createLocalOrder(this.testUtils.getLocalMemberId());

        this.orderController.activateOrder(localOrder);

        Mockito.when(this.cloudConnector.requestInstance(Mockito.any(Order.class)))
                .thenAnswer(
                        new Answer<String>() {
                            @Override
                            public String answer(InvocationOnMock invocation)
                                    throws InterruptedException {
                                Thread.sleep(TestUtils.DEFAULT_SLEEP_TIME);
                                return TestUtils.FAKE_INSTANCE_ID;
                            }
                        });
        //exercise
        this.thread = new Thread(this.processor);
        this.thread.start();
        Thread.sleep(OPEN_SLEEP_TIME);

        synchronized (localOrder) {
            Thread.sleep(OPEN_SLEEP_TIME);
            Assert.assertEquals(OrderState.SPAWNING, localOrder.getOrderState());
            localOrder.setOrderState(OrderState.OPEN);
        }

        //verify
        Assert.assertEquals(OrderState.OPEN, localOrder.getOrderState());
    }

    // test case: this method tests if, after starting a thread using an 
    // OpenProcessor instance, the method 'stop' stops correctly the thread
    @Test
    public void testStop() throws InterruptedException, FogbowException {
        this.thread = new Thread(this.processor);
        this.thread.start();
        
        while (!this.processor.isActive()) ;
        
        this.processor.stop();
        this.thread.join();
        
        Assert.assertFalse(this.thread.isAlive());
    }
    
    private boolean listIsEmpty(ChainedList<Order> list) {
        list.resetPointer();
        return list.getNext() == null;
    }
}
