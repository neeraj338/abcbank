package com.abcbank.accountmaintenance.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.abcbank.accountmaintenance.entity.Account;
import com.abcbank.accountmaintenance.repository.AccountRepository;
import com.abcbank.accountmaintenance.util.AppUtil;

@Service
public class AccountService {

	private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

	@Autowired
	private AccountRepository accountRepo;

	@PersistenceContext
	private EntityManager entityManager;

	public List<Account> getAccounts() {
		return accountRepo.findAll();
	}


	@Transactional
	public Account saveUpdate(Account entity) {
		return accountRepo.save(entity);
	}

	public Account findByAccountNumber(String accountNumber) {
		Account account = accountRepo.findByAccountNumber(accountNumber);
		if (AppUtil.isNullObject(account)) {
			String errMessage = String.format("No entity found for Account# %s", accountNumber);
			logger.error(errMessage);
			throw new EntityNotFoundException(errMessage);
		}
		return account;
	}
}
