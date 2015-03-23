package jpm.Prioritisers;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import jpm.ResourceScheduler.ResourceSchedulerMessage;
import jpm.interfaces.MessagePrioritiser;

public class GroupMessagePrioritiser implements MessagePrioritiser {

	// we need an insert-ordered list of groupIds, which contains a time ordered
	// list of Messages
	protected LinkedHashMap<String, ArrayList<ResourceSchedulerMessage>> groups;
	
	
	public GroupMessagePrioritiser() {
		
		// intialise the empty groups map
		groups = new LinkedHashMap<String, ArrayList<ResourceSchedulerMessage>>();
		
	}
	
	
	@Override
	public void queueMessage(ResourceSchedulerMessage message) throws Exception {
		
		// check if this groupId already exists
		String groupId = message.getGroupId();
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
	public void cancelGroup(String groupId) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void terminateGroup(String groupId) {
		// TODO Auto-generated method stub
		
	}

}
