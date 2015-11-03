package org.pazu.service;

import java.util.Collection;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;

import org.pazu.model.Greeting;
import org.pazu.repository.GreetingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class GreetingServiceBean implements GreetingService {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * The Spring Data repository for Greeting entities.
	 */
	@Autowired
	private GreetingRepository greetingRepository;

	@Override
	public Collection<Greeting> findAll() {
		logger.info("> findAll");

		Collection<Greeting> greetings = greetingRepository.findAll();

		logger.info("< findAll");
		return greetings;
	}

	@Override 
	@Cacheable(value = "greetingsCache", key = "#id")
	public Greeting findOne(Long id) {
		logger.info("> findOne id:{}", id);

		Greeting greeting = greetingRepository.findOne(id);

		logger.info("< findOne id:{}", id);
		return greeting;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	@CachePut(value = "greetingsCache", key = "#result.id")
	public Greeting create(Greeting greeting) {
		logger.info("> create");

		// Ensure the entity object to be created does NOT exist in the
		// repository. Prevent the default behavior of save() which will update
		// an existing entity if the entity matching the supplied id exists.
		if (greeting.getId() != null) {
			// Cannot create Greeting with specified ID value
			logger.error("Attempted to create a Greeting, but id attribute was not null.");
			throw new EntityExistsException(
					"The id attribute must be null to persist a new entity.");
		}

		Greeting savedGreeting = greetingRepository.save(greeting);

		logger.info("< create");
		return savedGreeting;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	@CachePut(value = "greetingsCache", key = "#greeting.id")
	public Greeting update(Greeting greeting) {
		logger.info("> update id:{}", greeting.getId());

		// Ensure the entity object to be updated exists in the repository to
		// prevent the default behavior of save() which will persist a new
		// entity if the entity matching the id does not exist
		Greeting greetingToUpdate = findOne(greeting.getId());
		if (greetingToUpdate == null) {
			// Cannot update Greeting that hasn't been persisted
			logger.error("Attempted to update a Greeting, but the entity does not exist.");
			throw new NoResultException("Requested entity not found.");
		}

		greetingToUpdate.setText(greeting.getText());
		Greeting updatedGreeting = greetingRepository.save(greetingToUpdate);

		logger.info("< update id:{}", greeting.getId());
		return updatedGreeting;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	@CacheEvict(value = "greetingsCache", key = "#id")
	public void delete(Long id) {
		logger.info("> delete id:{}", id);

		greetingRepository.delete(id);

		logger.info("< delete id:{}", id);
	}

	@Override
	@CacheEvict(value = "greetingsCache", allEntries = true)
	public void evictCache() {
		logger.info("> evictCache");
		logger.info("< evictCache");
	}

	// private static Long nextId;
	// private static Map<Long, Greeting> greetingMap;
	//
	// private static Greeting save(Greeting greeting) {
	//
	// if (greetingMap == null) {
	// greetingMap = new HashMap<Long, Greeting>();
	// nextId = 1L;
	// }
	//
	// // To update...
	// if (greeting.getId() != null) {
	// Greeting oldGreeting = greetingMap.get(greeting.getId());
	// if (oldGreeting == null) {
	// return null;
	// }
	// greetingMap.put(greeting.getId(), greeting);
	// return greeting;
	// }
	//
	// // To create...
	// greeting.setId(nextId);
	// nextId++;
	// greetingMap.put(greeting.getId(), greeting);
	// return greeting;
	// }
	//
	// private static boolean remove(Long id) {
	//
	// Greeting greeting = greetingMap.remove(id);
	// if (greeting == null) {
	// return false;
	// }
	//
	// return true;
	// }
	//
	// static {
	// Greeting g1 = new Greeting();
	// g1.setText("Hello 1");
	// save(g1);
	//
	// Greeting g2 = new Greeting();
	// g2.setText("Hello 2");
	// save(g2);
	//
	// }

}
