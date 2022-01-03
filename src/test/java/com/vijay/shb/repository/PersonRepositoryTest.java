package com.vijay.shb.repository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import com.vijay.shb.entity.Person;

@DataJpaTest
class PersonRepositoryTest {

	@Autowired
	private PersonRepository personRepository;

	@Test
	void verify_find_by_person_number() {
		final Person person = getPerson1();
		personRepository.save(person);
		final Optional<Person> result = personRepository.findByPersonalNumber("19711025-7735");
		assertThat(result.get().getName(), equalTo("Vijay1"));
	}

	@Test
	void verify_findAll() {
		personRepository.saveAll(Arrays.asList(getPerson1(), getPerson2(), getPerson3()));
		final List<Person> result = personRepository.findAll();

		assertThat(result.size(), equalTo(3));
	}

	@Test
	void verify_save() {
		final Person person = getPerson1();
		final Person found = personRepository.save(person);

		assertNotNull(found);
	}

	@Test
	void verify_duplicate_person_number_not_saved() {

		// Try adding 2 person with same person number. Should be big no no.
		final Person p1 = getPerson1();
		final Person p2 = getPerson1();

		// No problem in first person nummer
		personRepository.saveAndFlush(p1);

		assertThatThrownBy(() -> personRepository.saveAndFlush(p2))
									.isInstanceOf(DataIntegrityViolationException.class);
	}

	private Person getPerson1() {
		return new Person("19711025-7735", "Vijay1", "Address1");
	}

	private Person getPerson2() {
		return new Person("19711020-7735", "Vijay2", "Address2");
	}

	private Person getPerson3() {
		return new Person("19711022-7735", "Vijay3", "Address2");
	}

}
