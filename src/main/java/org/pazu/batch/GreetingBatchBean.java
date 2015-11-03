package org.pazu.batch;

import java.util.Collection;

import org.pazu.model.Greeting;
import org.pazu.service.GreetingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Profile("batch")
@Component
public class GreetingBatchBean {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private GreetingService greetingService;

//	@Scheduled(cron = "0,30 * * * * *")
	@Scheduled(cron = "${batch.greeting.cron}")
	public void cronjob() {
		logger.info("> cronjob");
		Collection<Greeting> greetings = greetingService.findAll();
		logger.info("There are {} greetings in the data store.",
				greetings.size());
		logger.info("< cronjob");
	}

	//@Scheduled(initialDelay = 5000, fixedRate = 15000)
	@Scheduled(initialDelayString = "${batch.greeting.fixeddelay}", fixedRateString =  "${batch.greeting.fixeddelay}")
	// start the cron job and count from starting to 15000 ms and then start the
	// cron job again.
	public void fixedRateJobWithInitialDelay() {
		logger.info(" > fixedRateJobWithInitialDelay");

		// Add scheduled logic here
		// Simulate job processing time
		long pause = 5000;
		long start = System.currentTimeMillis();

		logger.info("System.currentTimeMillis(): " + System.currentTimeMillis());
		do {
			if (start + pause < System.currentTimeMillis()) {
				break;
			}
		} while (true);
		logger.info("Processing time was {} seconds.", pause / 1000);

		logger.info(" < fixedRateJobWithInitialDelay");
	}

	//@Scheduled(initialDelay = 5000, fixedDelay = 15000)
	@Scheduled(initialDelayString = "${batch.greeting.initialdelay}", fixedDelayString = "${batch.greeting.fixeddelay}")
	// finish the cronjob and trigger the next round after 15000
	public void fixedDelayJobWithInitialDelay() {
		logger.info("> fixedDelayJobWithInitialDelay");

		// Add scheduled logic here
		// Simulate job processing time
		long pause = 5000;
		long start = System.currentTimeMillis();
		do {
			if (start + pause < System.currentTimeMillis()) {
				break;
			}
		} while (true);
		logger.info("Processing time was {} seconds.", pause / 1000);

		logger.info("< fixedDelayJobWithInitialDelay");
	}

}
