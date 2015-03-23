package jpm.Gateway;

import jpm.interfaces.Gateway;
import jpm.interfaces.Message;

public class SimpleGateway implements Gateway {

	@Override
	public void send(Message message) {
		
		// do nothing other than set the message to be completed
		message.completed();
		
	}
	
	public boolean isAvailable() {
		return true;
	}

}
