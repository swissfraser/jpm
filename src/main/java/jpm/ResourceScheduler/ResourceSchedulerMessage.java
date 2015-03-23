package jpm.ResourceScheduler;

import jpm.interfaces.Message;

public class ResourceSchedulerMessage implements Message {

	protected ResourceScheduler resourceScheduler;
	
	protected String groupId;
	
	protected boolean completed;
	
	
	public ResourceSchedulerMessage(String groupId) {
		this.groupId = groupId;
		this.completed = false;
	}

	public ResourceScheduler getResourceScheduler() {
		return resourceScheduler;
	}

	public void setResourceScheduler(ResourceScheduler resourceScheduler) {
		this.resourceScheduler = resourceScheduler;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	@Override
	public void completed() {
		this.completed = true;
		this.resourceScheduler.messageComplete(this);
	}
	
	public boolean isCompleted() {
		return this.completed;
	}

}
