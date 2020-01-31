package com.abcbank.accountmaintenance.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abcbank.accountmaintenance.entity.Transaction;
import com.abcbank.accountmaintenance.entity.Transaction.TransactionType;
import com.abcbank.accountmaintenance.model.AccountTransactionHistory;
import com.abcbank.accountmaintenance.model.DepositToAccountModel;
import com.abcbank.accountmaintenance.model.TransactionHistory;
import com.abcbank.accountmaintenance.model.TransferToAccountModel;
import com.abcbank.accountmaintenance.model.WithdrawFromAccountModel;
import com.abcbank.accountmaintenance.service.AccountTransactionService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
@Api("Operations on Transaction")
@Validated
@CrossOrigin
public class TransactionController {

	@Autowired
	private AccountTransactionService txService;

	@Autowired
	private ModelMapper modelMapper;

	@ApiOperation(value = "withdraw amount")
	@PostMapping(value = "/withdraw")
	public ResponseEntity<?> withdraw(@Valid @RequestBody WithdrawFromAccountModel reqModel) {
		txService.withdrawal(reqModel.getFromAccountNumber(), reqModel.getWithdrawlAmount());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@ApiOperation(value = "deposit amount")
	@PostMapping(value = "/deposit")
	public ResponseEntity<?> deposit(@Valid @RequestBody DepositToAccountModel reqModel) {
		txService.deposit(reqModel.getDepositorAccountNumber(), reqModel.getDepositAmount());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@ApiOperation(value = "transfer amount to another account ")
	@PostMapping(value = "/transfer")
	public ResponseEntity<?> transfer(@Valid @RequestBody TransferToAccountModel requestModel) {
		txService.transfer(requestModel.getFromAccountNumber(), requestModel.getToAccountNumber(),
				requestModel.getTransferAmount());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@ApiOperation(value = "transaction history", response = TransactionHistory.class, responseContainer = "list")
	@GetMapping(value = "/transaction-history")
	public ResponseEntity<List<TransactionHistory>> txHistory() {
		List<TransactionHistory> txHistoryList = new ArrayList<TransactionHistory>();
		List<Transaction> transactionHistory = txService.getTransactionHistory();
		Map<Date, Map<String, List<Transaction>>> groupByDateAndAccount = transactionHistory
				.stream()
				.sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
				.collect(Collectors.groupingBy(tx -> tx.getTxDateWithoutTime(),
							Collectors.groupingBy(tx -> tx.getAccountNumber())));

		groupByDateAndAccount.forEach((k, v) -> {
			v.forEach((key, val) -> {
				Map<TransactionType, BigDecimal> descriminatorSum = val.stream()
						.collect(Collectors.groupingBy(Transaction::getDiscriminator,
								Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));
				
				txHistoryList.add(TransactionHistory.converter(k, key, descriminatorSum));
			});
		});
		txHistoryList.sort(Comparator.comparing(TransactionHistory::getTransactionDate).reversed());
		return new ResponseEntity<>(txHistoryList, HttpStatus.OK);
	}

	@ApiOperation(value = "account transaction history", response = AccountTransactionHistory.class, responseContainer = "list")
	@GetMapping(value = "/account-transaction-history")
	public ResponseEntity<List<AccountTransactionHistory>> acccountTxHistory() {
		List<AccountTransactionHistory> txHistoryList = new ArrayList<>();
		List<Transaction> transactionHistory = txService.getTransactionHistory();
		this.modelMapper.addConverter(AccountTransactionHistory.converter());

		transactionHistory.stream().sorted(Comparator.comparing(Transaction::getTransactionDate).reversed())
				.map(x -> this.modelMapper.map(x, AccountTransactionHistory.class))
				.sorted(Comparator.comparing(AccountTransactionHistory::getCreatedDate))
				.collect(Collectors.toCollection(() -> txHistoryList));

		return new ResponseEntity<>(txHistoryList, HttpStatus.OK);
	}
}
