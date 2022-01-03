package com.vijay.shb.repository;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import com.vijay.shb.entity.Person;
import com.vijay.shb.entity.PersonAccount;
import com.vijay.shb.model.AccountType;

@DataJpaTest
class PersonAccountRepositoryTest {

	@Autowired
	private PersonRepository personRepository;

	@Autowired
	private PersonAccountRepository personAccountRepository;
	
	private static final String PERSON_NUMMER = "20001127-7735";

	@Test
	void saving_duplicate_account_for_same_person_not_allowed() {

		final Person person = prepareMockdata(PERSON_NUMMER);

		final PersonAccount account3 = new PersonAccount(person, 100, AccountType.CURRENT,
				BigDecimal.valueOf(40000.50));

		assertThatThrownBy(() -> personAccountRepository.saveAndFlush(account3))
				.isInstanceOf(DataIntegrityViolationException.class);
	}

	@Test
	void fetchAll_returns_all_accounts_for_person() {

		prepareMockdata(PERSON_NUMMER);

		final List<PersonAccount> accounts = personAccountRepository.findAll();

		assertThat(accounts.size(), equalTo(2));
	}
	
	@Test
	void fetching_account_returns_person_data() {

		prepareMockdata(PERSON_NUMMER);

		final List<PersonAccount> accounts = personAccountRepository.findAll();
		
		Person p = accounts.get(0).getPerson();

		assertThat(p.getName(), equalTo("Vijay Thakre"));
	}
	
	@Test
	void verify_fetch_by_person_number() {

		prepareMockdata(PERSON_NUMMER);
		prepareMockdata("19990315-1354");

		final List<PersonAccount> accounts = personAccountRepository.findByPersonNummer(PERSON_NUMMER);
	
		final Optional<PersonAccount> currentAccount = accounts.stream()
				.filter(account -> account.getAccountType().equals(AccountType.CURRENT))
				.findAny();
		
		final Optional<PersonAccount> savingAccount = accounts.stream()
				.filter(account -> account.getAccountType().equals(AccountType.SAVINGS))
				.findAny();
		
		final Optional<PersonAccount> creditAccount = accounts.stream()
				.filter(account -> account.getAccountType().equals(AccountType.CREDIT))
				.findAny();
		
		assertTrue(currentAccount.isPresent());
		assertTrue(savingAccount.isPresent());
		assertFalse(creditAccount.isPresent());		
	}
	
	@Test
	void verify_fetch_by_account_number() {
		
		prepareMockdata(PERSON_NUMMER);
		
		final var someData = personAccountRepository.findAll().stream().findAny().get();
		
		final var accountDetails = personAccountRepository.findById(someData.getId());
		
		assertNotNull(accountDetails.get().getPerson());
		
	}

	private Person prepareMockdata(final String personNummer) {
		final Person person = new Person(personNummer, "Vijay Thakre", "Address1");

		final PersonAccount account1 = new PersonAccount(person, 100, AccountType.CURRENT,
				BigDecimal.valueOf(10000.50));
		final PersonAccount account2 = new PersonAccount(person, 200, AccountType.SAVINGS,
				BigDecimal.valueOf(10000.50));
	
		personRepository.saveAndFlush(person);
	
		personAccountRepository.saveAndFlush(account1);
		personAccountRepository.saveAndFlush(account2);

		return person;

	}

}
