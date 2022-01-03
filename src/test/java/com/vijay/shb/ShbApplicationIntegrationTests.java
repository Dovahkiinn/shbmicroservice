package com.vijay.shb;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vijay.shb.model.AccountRequest;
import com.vijay.shb.model.AccountType;

@SpringBootTest( webEnvironment = WebEnvironment.RANDOM_PORT,
				classes = ShbApplication.class)
@AutoConfigureMockMvc
class ShbApplicationIntegrationTests {

	@Autowired
	private MockMvc mockMvc;

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Test
	void verify_entire_flow_in_application() throws Exception {
		
		//all 3 layers are tested to be functioning. Individual unit tests will perform detail tests. 

		final var PERSON_NUMMER = "19991025-1234";
		final var NAME = "Jeff Bezos";

		final AccountRequest request = new AccountRequest(PERSON_NUMMER, NAME, "Mars", AccountType.CURRENT);

		final String requestAsJson = OBJECT_MAPPER.writeValueAsString(request);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/shb/skapakonto").content(requestAsJson)
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated()).andReturn();
		
		assertTrue(result.getResponse().getContentAsString().contains(PERSON_NUMMER));
		assertTrue(result.getResponse().getContentAsString().contains(NAME));
	}
}
