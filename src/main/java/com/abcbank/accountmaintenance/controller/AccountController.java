package com.abcbank.accountmaintenance.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abcbank.accountmaintenance.entity.Account;
import com.abcbank.accountmaintenance.service.AccountService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping(value = "/accounts", produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
@Api("Operations on Bank")
@Validated
@CrossOrigin
public class AccountController {

	@Autowired
	private AccountService accountService;

	@ApiOperation(value = "get all accounts ", response = Account.class, responseContainer = "list")
	@GetMapping
	public ResponseEntity<List<Account>> getAllAccount() {
		List<Account> accounts = accountService.getAccounts();
		return ResponseEntity.ok(accounts);
	}

	@ApiOperation(value = "get account for account number", response = Account.class)
	@GetMapping(value = "/{accountNumber}")
	public ResponseEntity<Account> getAccount(@NotBlank @PathVariable String accountNumber) {
		Account accounts = accountService.findByAccountNumber(accountNumber);

		return ResponseEntity.ok(accounts);
	}

	@ApiOperation(value = "open an account", response = Account.class)
	@PostMapping
	public ResponseEntity<Account> craeteAccount(@Valid @RequestBody Account account) {
		Account dbAccount = accountService.saveUpdate(account);
		return ResponseEntity.status(HttpStatus.CREATED).body(dbAccount);
	}

}
