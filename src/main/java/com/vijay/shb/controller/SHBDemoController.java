package com.vijay.shb.controller;

import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vijay.shb.exception.DuplicateUserAndAccountException;
import com.vijay.shb.model.Account;
import com.vijay.shb.model.AccountRequest;
import com.vijay.shb.service.AccountService;

import io.swagger.v3.oas.annotations.Operation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/shb")
public class SHBDemoController {
	
	@Autowired
	private AccountService accountService;	
	
	private Logger logger = LoggerFactory.getLogger(SHBDemoController.class);
	
	//TODO : implement proper controller exception handling. 
	
	//TODO: Instead of list of array the 2 getmappinng should return a wrapper Json with Person detail + accounts (as Account element array).
	@GetMapping("/hamtapersonkonto")
	@Operation(summary =  "Finds All account for a user", description = "Supply swedish person-nummer as input to find all accounts associated with the user.")
	public ResponseEntity<List<Account>> getAllAccounts(@RequestParam(required = true) String personnummer) {
		
		List<Account> accounts = new ArrayList<>();		
		
		try {			

			if (personnummer != null)
				accounts =  accountService.getAllAccounts(personnummer);		
			
			return new ResponseEntity<>(accounts, HttpStatus.OK);
			
		} catch (Exception e) {
			//YES YES, I have heard about the java log4j drcirity issue. 
			logger.error(String.format("'hamtapersonkonto' call failed with %s", e.getMessage()));
			return new ResponseEntity<>(accounts, HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/hamtakonto")
	@Operation(summary =  "Finds a single account for a user", description = "Supply account number as input to find details.")
	public ResponseEntity<Account> getAllAccounts(@RequestParam(required = true) long accountnumber) {
	
		try {
			
		    final var account =  accountService.getAccountByAccountNumber(accountnumber);		
			
			return new ResponseEntity<>(account.orElse(null), HttpStatus.OK);
			
		} catch (Exception e) {
			logger.error(String.format("'hamtakonto' call failed with %s", e.getMessage()));
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping(("/skapakonto"))
	@Operation(summary =  "Create a user or account or both", description = "Create a user or account or both.")
	public ResponseEntity<Account> createAccount(@Valid @RequestBody AccountRequest accountDetail ){
		
		try {
			final var createdAccount = accountService.createAccount(accountDetail);
			return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
		} catch (final DuplicateUserAndAccountException e) {
			logger.error(String.format("'skapakonto' call failed with %s", e.getMessage()));
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}	
		
	}
	
	

}
