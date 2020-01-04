package com.abcbank.accountmaintenance.service;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ValidationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abcbank.accountmaintenance.config.TransactionLock;
import com.abcbank.accountmaintenance.entity.Account;
import com.abcbank.accountmaintenance.entity.Transaction;
import com.abcbank.accountmaintenance.repository.TransactionRepository;

@Service
public class AccountTransactionService {

	private static final Logger logger = LoggerFactory.getLogger(AccountTransactionService.class);

	@Autowired
	private AccountService accountService;
	
	@Autowired
	private TransactionRepository txRepo;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private TransactionLock txLock;

	public <T> void refreshEntity(T entity) {
		this.entityManager.refresh(entity);
	}

	@Transactional
	public void withdrawal(String accountNumber, BigDecimal amount) {

		Account account = accountService.findByAccountNumber(accountNumber);
		// check sufficient balance before accruing lock: throw validation exception.
		insureBalance(amount, account);

		try {
			txLock.lock(accountNumber);
			// reload entity after lock - no other transaction performing transfer or withdraw at this point
			this.refreshEntity(account);
			// check sufficient balance after accruing lock - may be previous tx has already withdraw some amount
			insureBalance(amount, account);

			account.setBalance(account.getBalance().subtract(amount));
			Transaction tx = Transaction.builder().account(account).amount(amount).discriminator("C").build();
			account.getTransactions().add(tx);

			accountService.saveUpdate(account);
		}
		finally {
			txLock.unlock(accountNumber);
		}
	}

	@Transactional
	public void transfer(String fromAccountNumber, String toAccountNumber, BigDecimal amount) {
		Account fromAccount = accountService.findByAccountNumber(fromAccountNumber);
		// check sufficient balance before accruing lock
		insureBalance(amount, fromAccount);

		try {
			txLock.lock(fromAccountNumber);
			// reload entity after lock - no other transaction performing transfer or withdraw at this point
			this.refreshEntity(fromAccount);
			// check sufficient balance after accruing lock - may be previous tx has already withdraw some amount
			insureBalance(amount, fromAccount);

			BigDecimal subtractedAmt = fromAccount.getBalance().subtract(amount);
			fromAccount.setBalance(subtractedAmt);
			Transaction txCredit = Transaction.builder().account(fromAccount).amount(amount).discriminator("C").build();
			fromAccount.getTransactions().add(txCredit);

			accountService.saveUpdate(fromAccount);

			this.deposit(toAccountNumber, amount);
		}
		finally {
			txLock.unlock(fromAccountNumber);
		}
	}

	@Transactional
	public Account deposit(String accountNumber, BigDecimal amount) {
		Account account = accountService.findByAccountNumber(accountNumber);
		account.setBalance(account.getBalance().add(amount));
		Transaction tx = Transaction.builder().account(account).amount(amount).discriminator("D").build();
		account.getTransactions().add(tx);

		return this.accountService.saveUpdate(account);
	}
	

	@Transactional
	public List<Transaction> getTransactionHistory() {
		return this.txRepo.findAll();
	}

	private void insureBalance(BigDecimal amount, Account account) throws ValidationException {
		if (account.getBalance().compareTo(amount) < 0) {
			String message = String.format("insufficient balance ! for account# %s,  balance %s, transfer amount %s",
					account.getAccountNumber(), account.getBalance(), amount);
			logger.error(message);
			throw new ValidationException(message);
		}
	}

}
