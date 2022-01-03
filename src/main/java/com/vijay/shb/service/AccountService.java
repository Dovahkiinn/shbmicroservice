package com.vijay.shb.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.vijay.shb.controller.SHBDemoController;
import com.vijay.shb.entity.Person;
import com.vijay.shb.entity.PersonAccount;
import com.vijay.shb.exception.DuplicateUserAndAccountException;
import com.vijay.shb.model.Account;
import com.vijay.shb.model.AccountRequest;
import com.vijay.shb.repository.PersonAccountRepository;
import com.vijay.shb.repository.PersonRepository;

@Service
public class AccountService {

	private final PersonAccountRepository personAccountRepository;

	private final PersonRepository personRepository;
	
	private Logger logger = LoggerFactory.getLogger(AccountService.class);

	@Autowired
	public AccountService(final PersonRepository personRepository,
						final PersonAccountRepository personAccountRepository) {

		this.personAccountRepository = personAccountRepository;
		this.personRepository = personRepository;
	}

	@Transactional
	public Account createAccount(final AccountRequest account) throws DuplicateUserAndAccountException {

		final var personOpt = personRepository.findByPersonalNumber(account.personNummer());

		var person = new Person(account.personNummer(), account.name(), account.address());

		if (personOpt.isPresent()) {
			person = personOpt.get();
		} else {
			person = personRepository.saveAndFlush(person);
		}

		final var personAccount = new PersonAccount(person, 1000, account.accountType(), BigDecimal.ZERO);

		try {
			
			return translateEntityToWebResponse(personAccountRepository.saveAndFlush(personAccount));

		} catch (DataIntegrityViolationException ex) {
			logger.error(String.format("failed to save User or account %s", ex.getMessage()));
			throw new DuplicateUserAndAccountException("User and account already exist");
		}
	}

	private static Account translateEntityToWebResponse(final PersonAccount createdAccount) {

		return new Account(createdAccount.getPerson().getPersonalNumber(), 
				createdAccount.getPerson().getName(),
				createdAccount.getId(), 
				createdAccount.getAccountType(), 
				createdAccount.getAmount());
	}

	public List<Account> getAllAccounts(final String personNumber) {
		
		final var accountsPerPerson = personAccountRepository.findByPersonNummer(personNumber);
		
		return accountsPerPerson.stream().map(AccountService::translateEntityToWebResponse).collect(Collectors.toList());
	}

	public Optional<Account> getAccountByAccountNumber(long accountNumber) {	
		
		final var result = personAccountRepository.findById(accountNumber);
		
		if(result.isPresent()) {
			return Optional.of(translateEntityToWebResponse(result.get()));
		}			
		
		return Optional.empty();
	}

}
