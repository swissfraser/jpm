package jpm;

import java.util.ArrayList;
import java.util.List;

import jpm.Gateway.SimpleGateway;
import jpm.Gateway.SleepyGateway;
import jpm.Prioritisers.DeluxeMessagePrioritiser;
import jpm.ResourceScheduler.ResourceScheduler;
import jpm.ResourceScheduler.ResourceSchedulerMessage;
import jpm.interfaces.Gateway;
import junit.framework.TestCase;

public class DeluxeTest extends TestCase {

	/**
	 * Ensure that if we cancel a group, all messages of that group are removed
	 * from the queue and no new items will be added to the queue.
	 */
	public void testGroupCancelling() {

		List<Gateway> gateways = new ArrayList<Gateway>();
		gateways.add(new SleepyGateway(50));

		DeluxeMessagePrioritiser prioritiser = new DeluxeMessagePrioritiser();

		ResourceScheduler resourceScheduler = new ResourceScheduler(gateways,
				prioritiser);

		try {

			// first message will go onto the gateway
			resourceScheduler.addMessage(new ResourceSchedulerMessage("A"));

			// stick the next messages onto the queue
			resourceScheduler.addMessage(new ResourceSchedulerMessage("B"));

			// cancel group B
			prioritiser.cancelGroup("B");

			resourceScheduler.addMessage(new ResourceSchedulerMessage("A"));
			resourceScheduler.addMessage(new ResourceSchedulerMessage("D"));
			resourceScheduler.addMessage(new ResourceSchedulerMessage("D"));
			// this message should not get through due to being cancelled
			resourceScheduler.addMessage(new ResourceSchedulerMessage("B"));
			resourceScheduler.addMessage(new ResourceSchedulerMessage("A"));

			// give it 2 seconds....
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
			assert (false);
		}

		String completedOrder = "";
		for (ResourceSchedulerMessage message : resourceScheduler
				.getCompletedMessages()) {
			completedOrder += message.getGroupId();
		}

		// no B's should have been processed
		assertEquals(completedOrder, "AAADD");

	}

	/**
	 * Ensure that if we terminate a group, subsequent attempts to add a message
	 * of that group will throw an exception
	 */
	public void testGroupTermination() {

		List<Gateway> gateways = new ArrayList<Gateway>();
		gateways.add(new SimpleGateway());

		DeluxeMessagePrioritiser prioritiser = new DeluxeMessagePrioritiser();

		ResourceScheduler resourceScheduler = new ResourceScheduler(gateways,
				prioritiser);

		try {

			// terminate all group A messages
			prioritiser.terminateGroup("A");

			// first message will throw an exception
			resourceScheduler.addMessage(new ResourceSchedulerMessage("A"));
			fail("Exception should have been thrown!");
			
			
		} catch (Exception e) {
			assertEquals(e.getClass().getName(), "jpm.Exceptions.GroupTerminatedException");
		}

	}

}
