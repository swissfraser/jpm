package jpm.interfaces;

public interface Gateway {
	void send(Message message);
	
	boolean isAvailable();
}
