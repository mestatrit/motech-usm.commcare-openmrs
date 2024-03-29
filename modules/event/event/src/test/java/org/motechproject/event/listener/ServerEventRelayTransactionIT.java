package org.motechproject.event.listener;

import org.apache.activemq.command.ActiveMQQueue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.OutboundEventGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.jms.JMSException;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/*.xml"})
public class ServerEventRelayTransactionIT {
    @Autowired
    OutboundEventGateway outboundEventGateway;
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    CachingConnectionFactory connectionFactory;
    @Autowired
    EventHandlerForServerEventRelayTransactionIT handler;

    @Value("${redeliveryDelayInMillis}")
    private int redeliveryDelayInMillis;

    ActiveMQQueueExplorer queueExplorer;
    private ActiveMQQueue eventQueue;

    @Before
    public void setUp() throws JMSException {
        handler.setupForFailure(false);
        queueExplorer = new ActiveMQQueueExplorer(connectionFactory);
        eventQueue = (ActiveMQQueue) applicationContext.getBean("eventQueue");
        queueExplorer.clear(eventQueue);
    }

    @Test
    public void successfulHandlingOfEventShouldDestroyTheEvent() throws Exception {
        int numberOfMessagesInQueue = queueExplorer.queueSize(eventQueue);
        MotechEvent motechEvent = new MotechEvent(EventHandlerForServerEventRelayTransactionIT.SUCCESSFUL_EVENT_SUBJECT);
        outboundEventGateway.sendEventMessage(motechEvent);
        Thread.sleep(redeliveryDelayInMillis);
        assertEquals(0, queueExplorer.queueSize(eventQueue) - numberOfMessagesInQueue);
    }

    @Test
    public void shouldInvokeListenerConcurrently() throws Exception {
        int numberOfMessagesInQueue = queueExplorer.queueSize(eventQueue);
        MotechEvent motechEvent = new MotechEvent(EventHandlerForServerEventRelayTransactionIT.LONG_RUNNING_PROCESS);
        outboundEventGateway.sendEventMessage(motechEvent);
        outboundEventGateway.sendEventMessage(motechEvent);
        Thread.sleep(EventHandlerForServerEventRelayTransactionIT.TASK_DURATION * 1000 + 1000);
        assertEquals(0, queueExplorer.queueSize(eventQueue) - numberOfMessagesInQueue);
    }

    @Test
    public void failureToHandleEventShouldNotDestroyTheEvent() throws Exception {
        handler.setupForFailure(true);
        MotechEvent motechEvent = new MotechEvent(EventHandlerForServerEventRelayTransactionIT.FAILING_EVENT_SUBJECT);
        outboundEventGateway.sendEventMessage(motechEvent);
        Thread.sleep(redeliveryDelayInMillis);
        assertEquals(1, queueExplorer.queueSize(eventQueue));
    }

    @Test
    public void failedMessagesAreRedelivered() throws Exception {
        handler.setupForFailure(true);
        MotechEvent motechEvent = new MotechEvent(EventHandlerForServerEventRelayTransactionIT.FAILING_EVENT_SUBJECT);
        outboundEventGateway.sendEventMessage(motechEvent);
        Thread.sleep(redeliveryDelayInMillis * 2);
        int retries = handler.retries();
        assertEquals(Integer.toString(retries), true, retries >= 2);
    }

    @Test
    public void failedMessageOnceHandledCorrectlySecondTime() throws Exception {
        handler.setupForFailure(true);
        MotechEvent motechEvent = new MotechEvent(EventHandlerForServerEventRelayTransactionIT.FAILING_EVENT_SUBJECT);
        outboundEventGateway.sendEventMessage(motechEvent);
        Thread.sleep(redeliveryDelayInMillis);
        handler.setupForFailure(false);
        Thread.sleep(redeliveryDelayInMillis * 2);
        assertEquals(true, handler.retries() <= 1);
    }

    @After
    public void tearDown() throws JMSException {
        if (queueExplorer != null) queueExplorer.close();
    }
}
