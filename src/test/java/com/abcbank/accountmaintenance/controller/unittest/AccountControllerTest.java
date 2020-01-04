package com.abcbank.accountmaintenance.controller.unittest;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.abcbank.accountmaintenance.app.AbcBankApplication;
import com.abcbank.accountmaintenance.controller.AccountController;
import com.abcbank.accountmaintenance.entity.Account;
import com.abcbank.accountmaintenance.service.AccountService;
import com.abcbank.accountmaintenance.util.AppUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { AbcBankApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = { "test" })
public class AccountControllerTest {

	@LocalServerPort
	int randomServerPort;

	MockMvc mockMvc;

	@Autowired
	private AccountController accountController;

	@MockBean
	private AccountService accountService;


	@Autowired
	protected WebApplicationContext wac;

	@Autowired
	private TestRestTemplate testRestTemplate;

	private Account account = null;
	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.standaloneSetup(this.accountController)
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();// Standalone context

		account = Account.builder().acountHolder("test user").balance(new BigDecimal("89.00")).build();
	}

	@Test
	public void createAccountTest() throws Exception {
		Mockito.when(accountService.saveUpdate(Mockito.any())).thenReturn(account);
		Mockito.when(accountService.findByAccountNumber(Mockito.any())).thenReturn(account);
		RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/accounts")
				.accept(MediaType.APPLICATION_JSON, MediaType.ALL);
		MvcResult result = mockMvc.perform(requestBuilder).andReturn();
		ResponseEntity<Account> responseEntity = this.testRestTemplate.exchange(
				"/accounts/" + account.getAccountNumber(),
				HttpMethod.GET, AppUtil.getHttpHeader(),
				Account.class);
		Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		Assert.assertEquals(account, responseEntity.getBody());
	}

}
