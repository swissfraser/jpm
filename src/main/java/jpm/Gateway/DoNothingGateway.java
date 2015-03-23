package jpm.Gateway;

import jpm.interfaces.Gateway;
import jpm.interfaces.Message;

public class DoNothingGateway implements Gateway {

	Message message = null;
	
	@Override
	public void send(Message message) {
		// as the name suggests, do nothing.  This implementation is to help test
		// how messages get queued up.
		this.message = message;

	}

	@Override
	public boolean isAvailable() {
		if(message==null) return true;
		return false;
	}

}
