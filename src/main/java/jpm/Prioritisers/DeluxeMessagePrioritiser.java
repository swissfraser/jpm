package jpm.Prioritisers;

import java.util.ArrayList;
import java.util.HashSet;

import jpm.Exceptions.GroupTerminatedException;
import jpm.ResourceScheduler.ResourceSchedulerMessage;

public class DeluxeMessagePrioritiser extends GroupMessagePrioritiser {

	protected HashSet<String> cancelled = new  HashSet<String>();
	protected HashSet<String> terminated = new  HashSet<String>();
	
	
	@Override
	public void queueMessage(ResourceSchedulerMessage message) throws Exception {
		
		
		String groupId = message.getGroupId();
		
		if(terminated.contains(groupId)) {
			throw new GroupTerminatedException();
		}
		
		if(cancelled.contains(groupId))
		{
			// dont add any messages from cancelled groups
			return;
		}
		
		// check if this groupId already exists
		if(groups.containsKey(groupId)) {
			// it exists, so append this message to the correct array
			groups.get(groupId).add(message);
		} else {
			// its a new groupId, create a new arrayList and add it to the map
			ArrayList<ResourceSchedulerMessage> messageList = new ArrayList<ResourceSchedulerMessage>();
			messageList.add(message);
			groups.put(groupId, messageList);
		}

	}

	@Override
	public ResourceSchedulerMessage getNextMessage() {

		// find the first relevant group which isn't empty
		for(ArrayList<ResourceSchedulerMessage> messageList : groups.values()) {
			
			if(!messageList.isEmpty()) {
				
				// pop the first message from the START of the message list
				return messageList.remove(0);
			}
			
		}
		return null;
	}

	@Override
	public Integer getQueueSize() {
		
		Integer queueSize = 0;
		for(ArrayList<ResourceSchedulerMessage> messageList : groups.values()) {
			queueSize += messageList.size();
		}
		return queueSize;
	}

	@Override
	public synchronized void cancelGroup(String groupId) {
		this.groups.remove(groupId);
		cancelled.add(groupId);
	}

	@Override
	public synchronized void terminateGroup(String groupId) {
		terminated.add(groupId);
	}
	
	

}
