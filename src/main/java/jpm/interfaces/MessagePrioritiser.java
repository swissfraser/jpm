package jpm.interfaces;

import jpm.ResourceScheduler.ResourceSchedulerMessage;

public interface MessagePrioritiser {

	public void queueMessage(ResourceSchedulerMessage message) throws Exception;
	
	public ResourceSchedulerMessage getNextMessage();
	
	public Integer getQueueSize();
	
	
	public void cancelGroup(String groupId);
	
	public void terminateGroup(String groupId);
}
