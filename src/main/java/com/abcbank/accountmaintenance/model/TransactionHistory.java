package com.abcbank.accountmaintenance.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Map;

import com.abcbank.accountmaintenance.entity.Transaction.TransactionType;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Builder
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@JsonInclude(JsonInclude.Include.ALWAYS)
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TransactionHistory implements Serializable {

	private static final long serialVersionUID = 1L;

	private String accountNumber;

	private BigDecimal withdrawl;

	private BigDecimal deposit;

	private Date transactionDate;


	public static TransactionHistory converter(Date txDate, String accountNumber,
			Map<TransactionType, BigDecimal> descriminatorSum) {
		TransactionHistory destination = new TransactionHistory();
		destination.setAccountNumber(accountNumber);
		destination.setDeposit(
				descriminatorSum.get(TransactionType.CREDIT) != null 
				? descriminatorSum.get(TransactionType.CREDIT).setScale(2, RoundingMode.HALF_UP)
						: descriminatorSum.get(TransactionType.CREDIT));
		BigDecimal debitAmount = descriminatorSum.get(TransactionType.DEBIT);
		destination.setWithdrawl(debitAmount != null ? debitAmount.setScale(2, RoundingMode.HALF_UP).negate() : debitAmount);
		destination.setTransactionDate(txDate);
		return destination;
	}

}
