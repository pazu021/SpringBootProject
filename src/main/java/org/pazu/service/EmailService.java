package org.pazu.service;

import java.util.concurrent.Future;

import org.pazu.model.Greeting;

public interface EmailService {

	Boolean send(Greeting greeting);
	
	void sendAsync(Greeting greeting);
	
	Future<Boolean> sendAsyncWithResult(Greeting greeting);
	
}
