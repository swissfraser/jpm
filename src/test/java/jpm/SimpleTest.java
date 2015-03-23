package jpm;

import java.util.ArrayList;
import java.util.List;

import jpm.Gateway.DoNothingGateway;
import jpm.Gateway.SimpleGateway;
import jpm.Gateway.SleepyGateway;
import jpm.Prioritisers.SimpleMessagePrioritiser;
import jpm.ResourceScheduler.ResourceScheduler;
import jpm.ResourceScheduler.ResourceSchedulerMessage;
import jpm.interfaces.Gateway;
import junit.framework.TestCase;

/**
 * 
 * Test that we can create a resourceScheduler which makes use of one or more
 * very simple gateway objects and a very simple last-in-first-out prioritiser.
 *
 */
public class SimpleTest extends TestCase {

	// test we can create a scheduler with a list of gateways and a prioritiser
	public void testCreateResourceScheduler() {

		try {
			List<Gateway> gateways = new ArrayList<Gateway>();

			SimpleMessagePrioritiser prioritiser = new SimpleMessagePrioritiser();

			ResourceScheduler resourceScheduler = new ResourceScheduler(
					gateways, prioritiser);
			// make a call just to remove the warning that the object is never used.
			resourceScheduler.getAvailableGatewayCount();
			
		} catch (Exception e) {
			fail("Could not create resourceScheduler");
		}

	}

	/**
	 * 
	 * Ensure that messages do actually go onto a queue if no gateways
	 * are available at the time of adding
	 * 
	 */
	public void testMessagesGetAddedToTheQueue() throws Exception {

		List<Gateway> gateways = new ArrayList<Gateway>();
		gateways.add(new DoNothingGateway());

		SimpleMessagePrioritiser prioritiser = new SimpleMessagePrioritiser();

		ResourceScheduler resourceScheduler = new ResourceScheduler(gateways,
				prioritiser);

		// 1 gateway should be available
		assertEquals(resourceScheduler.getAvailableGatewayCount(), (Integer) 1);

		// first message will go onto the DoNothingGateway
		resourceScheduler.addMessage(new ResourceSchedulerMessage("groupId"));

		// 0 gateways should be available
		assertEquals(resourceScheduler.getAvailableGatewayCount(), (Integer) 0);
		// nothing should be left on the queue
		assertEquals(prioritiser.getQueueSize(), (Integer) 0);

		// second message will go onto the queue
		resourceScheduler.addMessage(new ResourceSchedulerMessage("groupId"));
		assertEquals(prioritiser.getQueueSize(), (Integer) 1);
	}

	/**
	 *  Make sure that with more than one gateway, messages still get processed
	 *  and queued as expected
	 */
	public void testMessagesGetAddedToTheQueueWithMultipleGateways() throws Exception {

		List<Gateway> gateways = new ArrayList<Gateway>();
		gateways.add(new DoNothingGateway());
		gateways.add(new DoNothingGateway());

		SimpleMessagePrioritiser prioritiser = new SimpleMessagePrioritiser();

		ResourceScheduler resourceScheduler = new ResourceScheduler(gateways,
				prioritiser);

		// 2 gateways should be available
		assertEquals(resourceScheduler.getAvailableGatewayCount(), (Integer) 2);

		// first message will go onto the DoNothingGateway
		resourceScheduler.addMessage(new ResourceSchedulerMessage("groupId"));

		// 1 gateway should be available
		assertEquals(resourceScheduler.getAvailableGatewayCount(), (Integer) 1);

		// second message will go onto the remaining gateway
		resourceScheduler.addMessage(new ResourceSchedulerMessage("groupId"));

		// nothing should be left on the queue
		assertEquals(prioritiser.getQueueSize(), (Integer) 0);

		// third message will go onto the queue
		resourceScheduler.addMessage(new ResourceSchedulerMessage("groupId"));

		// queue should contain the third messsage
		assertEquals(prioritiser.getQueueSize(), (Integer) 1);

		// zero gateways should be available
		assertEquals(resourceScheduler.getAvailableGatewayCount(), (Integer) 0);

	}

	/**
	 * use a SimpleGateway this time, which immediately marks the message
	 * as being completed and frees up the gateway for reuse
	 */
	public void testMessagesGetRemovedFromTheQueue() {

		List<Gateway> gateways = new ArrayList<Gateway>();
		gateways.add(new SimpleGateway());

		SimpleMessagePrioritiser prioritiser = new SimpleMessagePrioritiser();

		ResourceScheduler resourceScheduler = new ResourceScheduler(gateways,
				prioritiser);

		// 1 gateway should be available
		assertEquals(resourceScheduler.getAvailableGatewayCount(), (Integer) 1);

		// first message will go onto the SimpleGateway
		ResourceSchedulerMessage testMessage = new ResourceSchedulerMessage(
				"groupId");
		try {
			resourceScheduler.addMessage(testMessage);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Gateways should be available again immediately
		assertEquals(resourceScheduler.getAvailableGatewayCount(), (Integer) 1);

		// nothing should be left on the queue
		assertEquals(prioritiser.getQueueSize(), (Integer) 0);

		// ensure message is completed
		assertTrue(testMessage.isCompleted());

	}

	/**
	 *  Test the addMessages function to add a load of messages and make sure
	 *  they all get processed. Use the sleepyGateway to pretend that something
	 *  is actually happening.
	 */
	public void testAddingListOfMessages() {

		List<Gateway> gateways = new ArrayList<Gateway>();

		// three gateways, make the first one slow on the assumption
		// that the other two will do all the processing, to pretend
		// its 'blocked'
		gateways.add(new SleepyGateway(1000));
		gateways.add(new SleepyGateway(5));
		gateways.add(new SleepyGateway(5));

		SimpleMessagePrioritiser prioritiser = new SimpleMessagePrioritiser();

		ResourceScheduler resourceScheduler = new ResourceScheduler(gateways,
				prioritiser);

		// build a list of messages
		List<ResourceSchedulerMessage> messages = new ArrayList<ResourceSchedulerMessage>();
		int messageCount = 100;
		for (int count = 0; count < messageCount; count++) {
			ResourceSchedulerMessage testMessage = new ResourceSchedulerMessage(
					"" + count);
			messages.add(testMessage);
		}
		
		// add the list of messages in one call
		try {
			resourceScheduler.addMessages(messages);
			// give it 2 seconds, this should be plenty time for the first
			// sleepyGateway to finish its 1 second wait
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
			assert (false);
		}

		// make sure all the messages were completed
		assertEquals(resourceScheduler.completedMessageCount(),
				(Integer) (messageCount));

	}

}
