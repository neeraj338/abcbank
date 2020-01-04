package com.abcbank.accountmaintenance.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.abcbank.accountmaintenance.app.AbcBankApplication;
import com.abcbank.accountmaintenance.config.RepositotyTestConfiguration;
import com.abcbank.accountmaintenance.entity.Account;
import com.abcbank.accountmaintenance.entity.Transaction;
import com.abcbank.accountmaintenance.repository.AccountRepository;
import com.abcbank.accountmaintenance.repository.TransactionRepository;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { AbcBankApplication.class,
		RepositotyTestConfiguration.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = { "test" })
public class TransactionServiceTest {

	@Autowired
	private AccountTransactionService txService;

	@Autowired
	private TransactionRepository txRepo;

	@Autowired
	private AccountRepository accountRepo;

	private Account account1 = null;

	private Account account2 = null;

	private Account account3 = null;

	private EntityManager entityManager = Mockito.mock(EntityManager.class);

	@Before
	public void setup() {

		account1 = Account.builder().accountNumber("A1").acountHolder("test user").balance(new BigDecimal("89.00"))
				.transactions(new ArrayList<>())
				.build();
		account2 = Account.builder().accountNumber("A2").acountHolder("test user2").balance(new BigDecimal("89.00"))
				.transactions(new ArrayList<>())
				.build();
		account3 = Account.builder().accountNumber("A3").acountHolder("test user3").balance(new BigDecimal("89.00"))
				.transactions(new ArrayList<>())
				.build();
	}

	@After
	public void validate() {
		validateMockitoUsage();
	}

	@Test
	public void getTransactionHistoryTest() {
		List<Transaction> list = new ArrayList<Transaction>();
		Transaction a1 = Transaction.builder()
				.account(account1)
				.amount(new BigDecimal("87.90")).discriminator("C").build();
				
		Transaction a2 = Transaction.builder().account(account2).amount(new BigDecimal("54.70")).discriminator("D")
				.build();
		Transaction a3 = Transaction.builder().account(account3).amount(new BigDecimal("54.70"))
				.discriminator("D").build();

		list.add(a1);
		list.add(a2);
		list.add(a3);

		when(txRepo.findAll()).thenReturn(list);

		// test
		List<Transaction> empList = txService.getTransactionHistory();

		assertEquals(3, empList.size());
	}

	@Test
	public void accountTranferA1toA2Test() {

		when(accountRepo.findByAccountNumber(account1.getAccountNumber())).thenReturn(account1);
		when(accountRepo.findByAccountNumber(account2.getAccountNumber())).thenReturn(account2);
		Mockito.doNothing().when(entityManager).refresh(Mockito.any());

		Mockito.when(accountRepo.save(Mockito.any())).thenAnswer(i -> i.getArguments()[0]);

		// replaced mocked entity manager using reflection
		ReflectionTestUtils.setField(txService, "entityManager", entityManager);
		
		BigDecimal a1InitialBalance = account1.getBalance();
		BigDecimal a2InitialBalance = account2.getBalance();

		// test
		txService.transfer(account1.getAccountNumber(), account2.getAccountNumber(),
				new BigDecimal("10.00"));

		assertEquals(a2InitialBalance.add(new BigDecimal("10.00")), account2.getBalance());
		assertEquals(a1InitialBalance.subtract(new BigDecimal("10.00")), account1.getBalance());

	}

	@Test
	public void withdrawTest() {

		when(accountRepo.findByAccountNumber(account1.getAccountNumber())).thenReturn(account1);
		Mockito.doNothing().when(entityManager).refresh(Mockito.any());

		Mockito.when(accountRepo.save(Mockito.any())).thenAnswer(i -> i.getArguments()[0]);

		// replaced mocked entity manager using reflection
		ReflectionTestUtils.setField(txService, "entityManager", entityManager);

		BigDecimal a1InitialBalance = account1.getBalance();

		// test
		txService.withdrawal(account1.getAccountNumber(), new BigDecimal("10.00"));

		assertEquals(a1InitialBalance.subtract(new BigDecimal("10.00")), account1.getBalance());

	}

	@Test
	public void depositTest() {

		Account dummyAcc = Account.builder().accountNumber("A1_dummy").acountHolder("dummy user")
				.balance(new BigDecimal("89.00")).transactions(new ArrayList<>()).build();
		// mock
		when(accountRepo.findByAccountNumber(dummyAcc.getAccountNumber())).thenReturn(dummyAcc);

		Mockito.when(accountRepo.save(Mockito.any())).thenAnswer(i -> i.getArguments()[0]);

		BigDecimal dummyAccInitialBalance = dummyAcc.getBalance();

		// test
		Account acc = txService.deposit(dummyAcc.getAccountNumber(), new BigDecimal("10.00"));

		assertEquals(dummyAccInitialBalance.add(new BigDecimal("10.00")), acc.getBalance());

	}
}
