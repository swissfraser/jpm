package jpm.Gateway;

import jpm.interfaces.Gateway;
import jpm.interfaces.Message;

public class SleepyGateway implements Gateway {

	protected Message message;
	
	protected Integer sleepyTime;
	
	public SleepyGateway(Integer timeToSleep) {
		this.sleepyTime = timeToSleep;
	}
	
	@Override
	public void send(Message message) {
		
		this.message = message;
		
		Thread thread = new Thread(new SleepyGatewayThread(message, sleepyTime));
		thread.start();
	}
	
	
	public boolean isAvailable() {
		if(this.message!=null && !this.message.isCompleted()) {
			return false;
		}
		return true;
	}
}
