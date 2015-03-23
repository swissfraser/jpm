package jpm.Gateway;

import jpm.interfaces.Message;

public class SleepyGatewayThread implements Runnable {

	protected Message message;
	protected Integer timeToSleep;
	
	public SleepyGatewayThread(Message message, Integer timeToSleep) {
		this.message = message;
		this.timeToSleep = timeToSleep;
	}
	
	@Override
	public void run() {

		try {
			// sleep for a few milliseconds to pretend we're doing something
			Thread.sleep(timeToSleep);
			message.completed();
		} catch (InterruptedException e) {
			e.printStackTrace();
			// if there's an error just set the message completed and move on...
			message.completed();
		}
		
	}

}
