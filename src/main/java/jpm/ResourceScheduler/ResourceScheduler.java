package jpm.ResourceScheduler;

import java.util.ArrayList;
import java.util.List;

import jpm.interfaces.Gateway;
import jpm.interfaces.MessagePrioritiser;

public class ResourceScheduler {

	protected MessagePrioritiser messagePrioritiser;

	protected List<Gateway> gateways;
	
	protected ArrayList<ResourceSchedulerMessage> completedMessageList;

	public ResourceScheduler(List<Gateway> gateways,
			MessagePrioritiser messagePrioritiser) {
		
		this.completedMessageList = new ArrayList<ResourceSchedulerMessage>();
		this.gateways = gateways;
		this.messagePrioritiser = messagePrioritiser;
	}

	public MessagePrioritiser getMessagePrioritiser() {
		return messagePrioritiser;
	}

	public void setMessagePrioritiser(MessagePrioritiser messagePrioritiser) {
		this.messagePrioritiser = messagePrioritiser;
	}

	public void addMessages(List<ResourceSchedulerMessage> messages) throws Exception {

		for (ResourceSchedulerMessage message : messages) {
			queueMessage(message);
		}
		processNextMessage();

	}

	public void addMessage(ResourceSchedulerMessage message) throws Exception {
		queueMessage(message);
		processNextMessage();
	}

	protected void queueMessage(ResourceSchedulerMessage message) throws Exception {
		// set the message's scheduler
		message.setResourceScheduler(this);
		// add this message to our queue
		getMessagePrioritiser().queueMessage(message);
	}

	protected synchronized void processNextMessage() {

		// check if we have any resources available right now
		Gateway gateway = getFirstAvailableGateway();
		if (gateway != null) {
			// we have a gateway, see if we have any messages to process
			ResourceSchedulerMessage nextMessage = getMessagePrioritiser()
					.getNextMessage();
			if (nextMessage != null) {
				gateway.send(nextMessage);
				
				// check if we can process any more messages
				processNextMessage();
			}
		}

	}

	public synchronized Gateway getFirstAvailableGateway() {

		for (Gateway gateway : gateways) {
			if (gateway.isAvailable()) {
				return gateway;
			}
		}

		// no gateways currently available
		return null;
	}

	public synchronized Integer getAvailableGatewayCount() {
		Integer count = 0;
		for (Gateway gateway : gateways) {
			if (gateway.isAvailable()) {
				++count;
			}
		}
		return count;
	}

	public synchronized void messageComplete(ResourceSchedulerMessage message) {
		this.completedMessageList.add(message);
		processNextMessage();
	}
	
	public synchronized Integer completedMessageCount() {
		return this.completedMessageList.size();
	}
	
	public synchronized ArrayList<ResourceSchedulerMessage> getCompletedMessages() {
		return this.completedMessageList;
	}
}
