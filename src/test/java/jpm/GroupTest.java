package jpm;

import java.util.ArrayList;
import java.util.List;

import jpm.Gateway.SleepyGateway;
import jpm.Prioritisers.GroupMessagePrioritiser;
import jpm.ResourceScheduler.ResourceScheduler;
import jpm.ResourceScheduler.ResourceSchedulerMessage;
import jpm.interfaces.Gateway;
import junit.framework.TestCase;

public class GroupTest extends TestCase {

	/**
	 * Using only one sleepGateway, ensure that the messages are processed by
	 * groupId, in this case they should be done alphabetically as they are
	 * being processed in serial.
	 */
	public void testMessagesGetAddedAccordingToGroup() {

		List<Gateway> gateways = new ArrayList<Gateway>();
		gateways.add(new SleepyGateway(50));

		GroupMessagePrioritiser prioritiser = new GroupMessagePrioritiser();

		ResourceScheduler resourceScheduler = new ResourceScheduler(gateways,
				prioritiser);

		try {

			// first message will go onto the gateway
			resourceScheduler.addMessage(new ResourceSchedulerMessage("A"));

			// stick the next messages onto the queue
			resourceScheduler.addMessage(new ResourceSchedulerMessage("B"));
			resourceScheduler.addMessage(new ResourceSchedulerMessage("A"));
			resourceScheduler.addMessage(new ResourceSchedulerMessage("D"));
			resourceScheduler.addMessage(new ResourceSchedulerMessage("D"));
			resourceScheduler.addMessage(new ResourceSchedulerMessage("B"));
			resourceScheduler.addMessage(new ResourceSchedulerMessage("A"));

			// give it 2 seconds....
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
			assert (false);
		}

		// iterate over all the completedMessage and extract a string of
		// groupIds
		String completedOrder = "";
		for (ResourceSchedulerMessage message : resourceScheduler
				.getCompletedMessages()) {
			completedOrder += message.getGroupId();
		}

		assertEquals(completedOrder, "AAABBDD");
	}

	/**
	 * Using several sleepGateways, ensure that the messages are processed by
	 * groupId. As there are multiple gateways multiple groups will run
	 * concurrently.
	 */
	public void testMessagesGetAddedAccordingToGroupWithMultipleGateways() {

		List<Gateway> gateways = new ArrayList<Gateway>();
		gateways.add(new SleepyGateway(20));
		gateways.add(new SleepyGateway(20));

		GroupMessagePrioritiser prioritiser = new GroupMessagePrioritiser();

		ResourceScheduler resourceScheduler = new ResourceScheduler(gateways,
				prioritiser);

		try {

			// first message will go onto the gateway
			resourceScheduler.addMessage(new ResourceSchedulerMessage("A"));

			// add tiny delay just to ensure the two sleepyGateways arent
			// running completely in parallel, otherwise predicting finish order
			// is not
			// possible as there's no telling exactly how the thread scheduler
			// and sleep
			// calls will behave.
			Thread.sleep(10);

			// second message will go onto the second gateway
			resourceScheduler.addMessage(new ResourceSchedulerMessage("B"));

			// stick the next messages onto the queue
			resourceScheduler.addMessage(new ResourceSchedulerMessage("A"));
			resourceScheduler.addMessage(new ResourceSchedulerMessage("D"));
			resourceScheduler.addMessage(new ResourceSchedulerMessage("D"));
			resourceScheduler.addMessage(new ResourceSchedulerMessage("B"));
			resourceScheduler.addMessage(new ResourceSchedulerMessage("A"));

			// give it 2 seconds....
			Thread.sleep(2000);
		} catch (Exception e) {
			e.printStackTrace();
			assert (false);
		}

		// iterate over all the completedMessage and extract a string of
		// groupIds
		String completedOrder = "";
		for (ResourceSchedulerMessage message : resourceScheduler
				.getCompletedMessages()) {
			completedOrder += message.getGroupId();
		}

		assertEquals(completedOrder, "ABAABDD");

	}

}
