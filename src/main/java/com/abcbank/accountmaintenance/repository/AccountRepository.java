package com.abcbank.accountmaintenance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.abcbank.accountmaintenance.entity.Account;

@Repository
public interface AccountRepository
		extends JpaRepository<Account, String>, CrudRepository<Account, String>, JpaSpecificationExecutor<Account> {

	public Account findByAccountNumber(String accountNumber);
}
