package com.abcbank.accountmaintenance.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.modelmapper.Converter;
import org.modelmapper.spi.MappingContext;

import com.abcbank.accountmaintenance.entity.Transaction;
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
public class AccountTransactionHistory implements Serializable {

	private static final long serialVersionUID = 1L;

	private String accountNumber;

	private BigDecimal amount;

	private Date createdDate;

	public static Converter<Transaction, AccountTransactionHistory> converter() {
		return new Converter<Transaction, AccountTransactionHistory>() {

			@Override
			public AccountTransactionHistory convert(MappingContext<Transaction, AccountTransactionHistory> context) {

				Transaction source = context.getSource();
				AccountTransactionHistory destination = new AccountTransactionHistory();
				destination.setAccountNumber(source.getAccount().getAccountNumber());
				BigDecimal amount = source.getAmount();
				if (null != amount) {
					BigDecimal scalledAmount = amount.setScale(2, RoundingMode.HALF_UP);
					destination.setAmount(
							source.getDiscriminator() == TransactionType.CREDIT ? scalledAmount : scalledAmount.negate());

				}
				destination.setCreatedDate(source.getAccount().getCreatedDate());
				return destination;
			}
		};
	}
}
