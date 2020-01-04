package com.abcbank.accountmaintenance.controller.intigrationtest;

import java.math.BigDecimal;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import com.abcbank.accountmaintenance.app.AbcBankApplication;
import com.abcbank.accountmaintenance.entity.Account;
import com.abcbank.accountmaintenance.model.AccountTransactionHistory;
import com.abcbank.accountmaintenance.model.DepositToAccountModel;
import com.abcbank.accountmaintenance.model.TransactionHistory;
import com.abcbank.accountmaintenance.model.TransferToAccountModel;
import com.abcbank.accountmaintenance.model.WithdrawFromAccountModel;
import com.abcbank.accountmaintenance.util.AppUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { AbcBankApplication.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class AbcBankControllerTest {

	@LocalServerPort
	int randomServerPort;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Test
	public void testTransferOneAccountToAnother() {
		Account a1 = Account.builder().acountHolder("A1").balance(new BigDecimal("29.80")).build();
		Account a2 = Account.builder().acountHolder("A2").balance(new BigDecimal("39.80")).build();

		// create account
		a1 = createAccount(a1);
		a2 = createAccount(a2);

		BigDecimal a1BalBeforeTransfer = a1.getBalance();
		String url = "/transfer";
		// URI (URL) parameters
		Map<String, String> uriParams = new HashMap<>();
		TransferToAccountModel requestObject = TransferToAccountModel.builder().fromAccountNumber(a1.getAccountNumber())
				.toAccountNumber(a2.getAccountNumber()).transferAmount(new BigDecimal("10.80")).build();


		URI uri = UriComponentsBuilder.fromUriString(url).buildAndExpand(uriParams).toUri();

		ResponseEntity<?> transferResponseEntity = this.testRestTemplate.exchange(uri, HttpMethod.POST,
				AppUtil.getEnityWithHttpHeader(requestObject), Object.class);

		Assert.assertEquals(HttpStatus.OK, transferResponseEntity.getStatusCode());

		// load account1 again
		ResponseEntity<Account> responseEntity = this.testRestTemplate.exchange("/accounts/" + a1.getAccountNumber(),
				HttpMethod.GET,
				AppUtil.getHttpHeader(),
				Account.class);
		// verify
		Assert.assertEquals(a1BalBeforeTransfer.subtract(new BigDecimal("10.80")),
				responseEntity.getBody().getBalance());

	}

	@Test
	public void testTransactionHistory() {
		Account a1 = Account.builder().acountHolder("A1").balance(new BigDecimal("29.80")).build();
		a1 = createAccount(a1);

		BigDecimal a1BalBeforeTransfer = a1.getBalance();
		String url = "/withdraw";
		// URI (URL) parameters
		Map<String, String> uriParams = new HashMap<>();
		WithdrawFromAccountModel requestObject = WithdrawFromAccountModel.builder()
				.fromAccountNumber(a1.getAccountNumber()).withdrawlAmount(new BigDecimal("10.80")).build();

		URI uri = UriComponentsBuilder.fromUriString(url).buildAndExpand(uriParams).toUri();

		ResponseEntity<?> transferResponseEntity = this.testRestTemplate.exchange(uri, HttpMethod.POST,
				AppUtil.getEnityWithHttpHeader(requestObject), Object.class);

		Assert.assertEquals(HttpStatus.OK, transferResponseEntity.getStatusCode());

		// load account1 again
		Account accout = getAccount(a1.getAccountNumber());
		// verify
		Assert.assertEquals(a1BalBeforeTransfer.subtract(new BigDecimal("10.80")),
				accout.getBalance());

		// verify the transaction history record
		ResponseEntity<List<TransactionHistory>> txHistoryResponse = this.testRestTemplate.exchange(
				"/transaction-history", HttpMethod.GET, AppUtil.getHttpHeader(),
				new ParameterizedTypeReference<List<TransactionHistory>>() {
				});
		List<TransactionHistory> txHistoryResponseBodyList = txHistoryResponse.getBody();
		Optional<TransactionHistory> txForCurrAccountOpn = txHistoryResponseBodyList.stream()
				.filter(x -> x.getAccountNumber().equals(accout.getAccountNumber())).findAny();

		Assert.assertTrue(txForCurrAccountOpn.isPresent());
		Assert.assertEquals(new BigDecimal("10.80").negate(), txForCurrAccountOpn.get().getWithdrawl());
	}

	@Test
	public void testForNotSufficeintBalValidationException() {
		Account a1 = Account.builder().acountHolder("A1").balance(new BigDecimal("29.80")).build();
		a1 = createAccount(a1);

		String url = "/withdraw";
		// URI (URL) parameters
		Map<String, String> uriParams = new HashMap<>();
		WithdrawFromAccountModel reqModel = WithdrawFromAccountModel.builder().fromAccountNumber(a1.getAccountNumber())
				.withdrawlAmount(new BigDecimal("30.00")).build();

		URI uri = UriComponentsBuilder.fromUriString(url).buildAndExpand(uriParams).toUri();

		ResponseEntity<?> transferResponseEntity = this.testRestTemplate.exchange(uri, HttpMethod.POST,
				AppUtil.getEnityWithHttpHeader(reqModel), Object.class);

		Assert.assertEquals(HttpStatus.CONFLICT, transferResponseEntity.getStatusCode());
	}

	@Test
	public void testAccountTransactionHistory() {
		Account a1 = Account.builder().acountHolder("A1").balance(new BigDecimal("29.80")).build();
		a1 = createAccount(a1);

		BigDecimal a1BalBeforeTransfer = a1.getBalance();
		String url = "/withdraw";
		// URI (URL) parameters
		Map<String, String> uriParams = new HashMap<>();
		WithdrawFromAccountModel requestObject = WithdrawFromAccountModel.builder()
				.fromAccountNumber(a1.getAccountNumber()).withdrawlAmount(new BigDecimal("10.80")).build();

		URI uri = UriComponentsBuilder.fromUriString(url).buildAndExpand(uriParams).toUri();

		ResponseEntity<?> transferResponseEntity = this.testRestTemplate.exchange(uri, HttpMethod.POST,
				AppUtil.getEnityWithHttpHeader(requestObject), Object.class);

		Assert.assertEquals(HttpStatus.OK, transferResponseEntity.getStatusCode());

		// deposit 10.80 back to account
		String urlDeposit = "/deposit";
		// URI (URL) parameters
		Map<String, String> uriParamsForDeposit = new HashMap<>();
		DepositToAccountModel requestObjectForDeposit = DepositToAccountModel.builder()
				.depositorAccountNumber(a1.getAccountNumber()).depositAmount(new BigDecimal("10.80")).build();

		this.testRestTemplate.exchange(
				UriComponentsBuilder.fromUriString(urlDeposit).buildAndExpand(uriParamsForDeposit).toUri(),
				HttpMethod.POST,
				AppUtil.getEnityWithHttpHeader(requestObjectForDeposit), Object.class);
		// load account1 again
		Account accout = getAccount(a1.getAccountNumber());
		// verify
		Assert.assertEquals(a1BalBeforeTransfer, accout.getBalance());

		// verify the transaction history record
		ResponseEntity<List<AccountTransactionHistory>> txHistoryResponse = this.testRestTemplate.exchange(
				"/account-transaction-history", HttpMethod.GET, AppUtil.getHttpHeader(),
				new ParameterizedTypeReference<List<AccountTransactionHistory>>() {
				});
		List<AccountTransactionHistory> txHistoryResponseBodyList = txHistoryResponse.getBody();
		List<AccountTransactionHistory> txAccountList = txHistoryResponseBodyList.stream()
				.filter(x -> x.getAccountNumber().equals(accout.getAccountNumber())).collect(Collectors.toList());

		Assert.assertEquals(2, txAccountList.size());
		// Assert.assertEquals(new BigDecimal("10.80").negate(), txForCurrAccountOpn.get().getWithdrawl());
	}

	private Account createAccount(Account acc) {
		ResponseEntity<Account> responseEntity = this.testRestTemplate.exchange("/accounts", HttpMethod.POST,
				AppUtil.getEnityWithHttpHeader(acc), Account.class);
		acc = responseEntity.getBody();
		return acc;
	}

	private Account getAccount(String accNumber) {
		ResponseEntity<Account> responseEntity = this.testRestTemplate.exchange("/accounts/" + accNumber,
				HttpMethod.GET, AppUtil.getHttpHeader(), Account.class);
		Account accout = responseEntity.getBody();
		return accout;
	}

}
