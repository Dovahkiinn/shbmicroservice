package com.vijay.shb.controller;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.shb.exception.DuplicateUserAndAccountException;
import com.vijay.shb.model.Account;
import com.vijay.shb.model.AccountRequest;
import com.vijay.shb.model.AccountType;
import com.vijay.shb.service.AccountService;

@WebMvcTest
class SHBDemoControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private AccountService accountService;

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Test
	void verify_createAccount() throws Exception {

		final var PERSON_NUMMER = "19991025-1234";
		final var NAME = "Jeff bezos";
		final long ACCOUNT_NUMBER = 1234567890;

		final AccountRequest request = new AccountRequest(PERSON_NUMMER, NAME, "Mars", AccountType.CURRENT);

		final String requestAsJson = OBJECT_MAPPER.writeValueAsString(request);

		final Account accountCreated = new Account(request.personNummer(), request.name(), ACCOUNT_NUMBER,
				request.accountType(), BigDecimal.ZERO);

		when(accountService.createAccount(request)).thenReturn(accountCreated);

		mockMvc.perform(MockMvcRequestBuilders.post("/shb/skapakonto").content(requestAsJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.personNumber").value(equalTo(PERSON_NUMMER)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.personName").value(equalTo(NAME)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.accountNumber").value(ACCOUNT_NUMBER))
				.andExpect(MockMvcResultMatchers.jsonPath("$.accountType").value(AccountType.CURRENT.name()));

	}

	@Test
	void verify_exception_createAccount() throws Exception {

		final var PERSON_NUMMER = "19991025-1234";
		final var NAME = "Jeff Bezos";

		final AccountRequest request = new AccountRequest(PERSON_NUMMER, NAME, "Mars", AccountType.CURRENT);

		final String requestAsJson = OBJECT_MAPPER.writeValueAsString(request);

		when(accountService.createAccount(request)).thenThrow(DuplicateUserAndAccountException.class);

		mockMvc.perform(MockMvcRequestBuilders.post("/shb/skapakonto")
				.content(requestAsJson)
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	void verify_getAllAccounts_for_customer() throws Exception {

		final var PERSON_NUMMER = "19991025-1234";
		final var NAME = "Bill Clinton";

		final var result = Arrays.asList(
					new Account(PERSON_NUMMER, NAME, 1000, AccountType.CURRENT, BigDecimal.TEN),
					new Account(PERSON_NUMMER, NAME, 2000, AccountType.SAVINGS, BigDecimal.ONE));

		when(accountService.getAllAccounts(PERSON_NUMMER)).thenReturn(result);

		this.mockMvc.perform(MockMvcRequestBuilders.get("/shb/hamtapersonkonto").param("personnummer", "19991025-1234"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE));

	}

}
