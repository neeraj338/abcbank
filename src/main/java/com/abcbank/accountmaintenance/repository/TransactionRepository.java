package com.abcbank.accountmaintenance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.abcbank.accountmaintenance.entity.Transaction;

@Repository
public interface TransactionRepository
		extends JpaRepository<Transaction, String>, CrudRepository<Transaction, String>,
		JpaSpecificationExecutor<Transaction> {


}
