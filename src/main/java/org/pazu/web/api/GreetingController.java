package org.pazu.web.api;

import java.util.Collection;
import java.util.concurrent.Future;

import org.pazu.model.Greeting;
import org.pazu.service.EmailService;
import org.pazu.service.GreetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController extends BaseController {

	@Autowired
	private GreetingService greetingService;

	@Autowired
	private EmailService emailService;

	@RequestMapping(value = "/api/greetings", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Collection<Greeting>> getGreetings() {
		Collection<Greeting> greetings = greetingService.findAll();
		return new ResponseEntity<Collection<Greeting>>(greetings,
				HttpStatus.OK);

	}

	@RequestMapping(value = "/api/greetings/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Greeting> getGreeting(@PathVariable("id") Long id) {

		Greeting greeting = greetingService.findOne(id);
		if (greeting == null) {
			return new ResponseEntity<Greeting>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Greeting>(greeting, HttpStatus.OK);

	}

	@RequestMapping(value = "/api/greetings", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Greeting> createGreeting(
			@RequestBody Greeting greeting) {

		Greeting savedGreeting = greetingService.create(greeting);
		return new ResponseEntity<Greeting>(savedGreeting, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/api/greetings/{id}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Greeting> updateGreeting(
			@RequestBody Greeting greeting) {

		Greeting updatedGreeting = greetingService.update(greeting);
		if (updatedGreeting == null) {
			return new ResponseEntity<Greeting>(
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<Greeting>(updatedGreeting, HttpStatus.OK);

	}

	@RequestMapping(value = "/api/greetings/{id}", method = RequestMethod.DELETE, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Greeting> deleteGreeting(@PathVariable Long id) {

		greetingService.delete(id);
		return new ResponseEntity<Greeting>(HttpStatus.NO_CONTENT);
	}

	/**
	 * Web service endpoint to fetch a single Greeting entity by primary key
	 * identifier and send it as an email.
	 * 
	 * If found, the Greeting is returned as JSON with HTTP status 200 and sent
	 * via Email.
	 * 
	 * If not found, the service returns an empty response body with HTTP status
	 * 404.
	 * 
	 * @param id
	 *            A Long URL path variable containing the Greeting primary key
	 *            identifier.
	 * @param waitForAsyncResult
	 *            A boolean indicating if the web service should wait for the
	 *            asynchronous email transmission.
	 * @return A ResponseEntity containing a single Greeting object, if found,
	 *         and a HTTP status code as described in the method comment.
	 */
	@RequestMapping(value = "/api/greetings/{id}/send", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Greeting> sendGreeting(
			@PathVariable("id") Long id,
			@RequestParam(value = "wait", defaultValue = "false") boolean waitForAsyncResult) {

		logger.info("> sendGreeting id:{}", id);

		Greeting greeting = null;

		try {
			greeting = greetingService.findOne(id);
			if (greeting == null) {
				logger.info("< sendGreeting id:{}", id);
				return new ResponseEntity<Greeting>(HttpStatus.NOT_FOUND);
			}

			if (waitForAsyncResult) {
				Future<Boolean> asyncResponse = emailService
						.sendAsyncWithResult(greeting);
				logger.info("- get aysnc result");
				boolean emailSent = asyncResponse.get();
				logger.info("- greeting email sent? {}", emailSent);
			} else {
				emailService.sendAsync(greeting);
			}
		} catch (Exception e) {
			logger.error("A problem occurred sending the Greeting.", e);
			return new ResponseEntity<Greeting>(
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		logger.info("< sendGreeting id:{}", id);
		return new ResponseEntity<Greeting>(greeting, HttpStatus.OK);
	}

}
