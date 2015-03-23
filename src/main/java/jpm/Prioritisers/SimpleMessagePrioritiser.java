package jpm.Prioritisers;

import java.util.ArrayList;

import jpm.ResourceScheduler.ResourceSchedulerMessage;
import jpm.interfaces.MessagePrioritiser;


// Simple LIFO implementation of the message prioritiser
public class SimpleMessagePrioritiser implements MessagePrioritiser {

	
	protected ArrayList<ResourceSchedulerMessage> messageList;
	
	public SimpleMessagePrioritiser() {
		this.messageList = new ArrayList<ResourceSchedulerMessage>();
	}
	
	
	@Override
	public synchronized ResourceSchedulerMessage getNextMessage() {

		if(messageList.isEmpty()) {
			return null;
		}
		return messageList.remove(0);
	}

	
	@Override
	public synchronized void queueMessage(ResourceSchedulerMessage message) {
		messageList.add(message);
	}


	@Override
	public synchronized Integer getQueueSize() {
		return this.messageList.size();
	}


	@Override
	public void cancelGroup(String groupId) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void terminateGroup(String groupId) {
		// TODO Auto-generated method stub
		
	}

	
}
