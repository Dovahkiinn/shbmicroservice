package com.vijay.shb.service;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

import com.vijay.shb.entity.Person;
import com.vijay.shb.entity.PersonAccount;
import com.vijay.shb.exception.DuplicateUserAndAccountException;
import com.vijay.shb.model.Account;
import com.vijay.shb.model.AccountRequest;
import com.vijay.shb.model.AccountType;
import com.vijay.shb.repository.PersonAccountRepository;
import com.vijay.shb.repository.PersonRepository;

@SpringBootTest
class AccountServiceTest {

	@Mock
	private PersonRepository personRepository;
	private @Mock PersonAccountRepository personAccountRepository;

	@Test
	void verify_new_customer_with_account_created_if_no_customer_existed() throws DuplicateUserAndAccountException {

		final AccountService accountService = new AccountService(personRepository, personAccountRepository);

		// Mock conditions
		when(personRepository.findByPersonalNumber(anyString())).thenReturn(Optional.empty());
		when(personRepository.saveAndFlush(any(Person.class))).then(AdditionalAnswers.returnsFirstArg());
		when(personAccountRepository.saveAndFlush(any(PersonAccount.class))).then(AdditionalAnswers.returnsFirstArg());

		// take action
		final AccountRequest accountRequest = new AccountRequest("20021025-1134", "Vijay Thakre", "Barcelona",
				AccountType.CURRENT);
		final Account accountCreated = accountService.createAccount(accountRequest);

		// test behaviour
		verify(personRepository, times(1)).saveAndFlush(any(Person.class)); // verify person was saved
		verify(personAccountRepository, times(1)).saveAndFlush(any(PersonAccount.class)); // verify Account of that
																							// person was saved
		assertThat(accountCreated.accountType(), equalTo(AccountType.CURRENT));
		assertThat(accountCreated.personName(), equalTo("Vijay Thakre"));
		assertThat(accountCreated.personNumber(), equalTo("20021025-1134"));
	}

	@Test
	void verify_account_created_if_customer_existed() throws DuplicateUserAndAccountException {

		final var accountService = new AccountService(personRepository, personAccountRepository);

		final var person = new Person("20101025-1000", "Vijay Thakre", "Stockholm");

		// Mock conditions
		when(personRepository.findByPersonalNumber(anyString())).thenReturn(Optional.of(person));
		when(personRepository.saveAndFlush(any(Person.class))).then(AdditionalAnswers.returnsFirstArg());
		when(personAccountRepository.saveAndFlush(any(PersonAccount.class))).then(AdditionalAnswers.returnsFirstArg());

		// take action
		final var accountRequest = new AccountRequest("20101025-1000", "Vijay Thakre..wont be saved",
				"Barcelona..wont be saved", AccountType.CURRENT);
		final var accountCreated = accountService.createAccount(accountRequest);

		// test behaviour
		verify(personRepository, never()).saveAndFlush(any(Person.class)); // verify person was not saved
		verify(personAccountRepository, times(1)).saveAndFlush(any(PersonAccount.class)); // verify Account of that
																							// person was saved
		assertThat(accountCreated.accountType(), equalTo(AccountType.CURRENT));
		assertThat(accountCreated.personName(), equalTo("Vijay Thakre"));
		assertThat(accountCreated.personNumber(), equalTo("20101025-1000"));
		assertThat(accountCreated.accountNumber(), instanceOf(Long.class));
	}

	@Test
	void verify_graceful_exit_if_customer_with_account_exists() throws DuplicateUserAndAccountException {

		final var accountService = new AccountService(personRepository, personAccountRepository);

		final var person = new Person("20101025-1000", "Vijay Thakre", "Stockholm");

		// Mock conditions
		when(personAccountRepository.saveAndFlush(any(PersonAccount.class)))
				.thenThrow(DataIntegrityViolationException.class);
		when(personRepository.findByPersonalNumber(anyString())).thenReturn(Optional.of(person));
		when(personRepository.saveAndFlush(any(Person.class))).then(AdditionalAnswers.returnsFirstArg());
		// take action
		final AccountRequest accountRequest = new AccountRequest("20101025-1000", "Vijay Thakre", "Moscow",
				AccountType.CURRENT);

		// verify
		assertThatThrownBy(() -> accountService.createAccount(accountRequest))
				.isInstanceOf(DuplicateUserAndAccountException.class);

	}

	@Test
	void verify_getAccounts_of_customer() {

		final var accountService = new AccountService(personRepository, personAccountRepository);

		// mock data
		final var person = new Person("20101025-1000", "Vijay Thakre", "Stockholm");

		final var personAccount1 = new PersonAccount(person, 100, AccountType.CREDIT, BigDecimal.TEN);
		final var personAccount2 = new PersonAccount(person, 101, AccountType.CURRENT, BigDecimal.ZERO);
		final var personAccount3 = new PersonAccount(person, 102, AccountType.SAVINGS, BigDecimal.ONE);

		// mock condition
		when(personAccountRepository.findByPersonNummer("20101025-1000"))
				.thenReturn(Arrays.asList(personAccount1, personAccount2, personAccount3));

		final var accounts = accountService.getAllAccounts(person.getPersonalNumber());

		assertThat(accounts.size(), equalTo(3));

		assertTrue(accounts.stream().anyMatch(a -> a.accountType().equals(AccountType.CREDIT)));
		assertTrue(accounts.stream().anyMatch(a -> a.accountType().equals(AccountType.CURRENT)));
		assertTrue(accounts.stream().anyMatch(a -> a.accountType().equals(AccountType.SAVINGS)));

		assertTrue(accounts.stream().anyMatch(a -> a.balance().equals(BigDecimal.ONE)));
		assertTrue(accounts.stream().anyMatch(a -> a.balance().equals(BigDecimal.TEN)));
		assertTrue(accounts.stream().anyMatch(a -> a.balance().equals(BigDecimal.ZERO)));
	}

	@Test
	void verify_fetch_by_account_number() {

		final var ACCOUNT_NUMBER = 10011l;
		final var PERSON_NUMBER = "19990203-3456";
		final var NAME = "Elon Musk";
		final var INITIAL_DEPOSIT = BigDecimal.valueOf(50000);

		final var accountService = new AccountService(personRepository, personAccountRepository);

		// mock data
		final Person person = new Person(PERSON_NUMBER, NAME, "Mars");
		final var optAccount = new PersonAccount(person, ACCOUNT_NUMBER, AccountType.CURRENT,INITIAL_DEPOSIT);

		// mock condition
		when(personAccountRepository.findById(ACCOUNT_NUMBER)).thenReturn(Optional.of(optAccount));

		// action
		final Optional<Account> optAccountResponse = accountService.getAccountByAccountNumber(ACCOUNT_NUMBER);

		assertFalse(optAccountResponse.isEmpty());
		assertThat(optAccountResponse.get().personName(), equalTo(NAME));
		assertThat(optAccountResponse.get().accountType(), equalTo(AccountType.CURRENT));
		assertThat(optAccountResponse.get().personNumber(), equalTo(PERSON_NUMBER));
		assertThat(optAccountResponse.get().balance(), equalTo(INITIAL_DEPOSIT));
	}
}
