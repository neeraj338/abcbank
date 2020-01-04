package com.abcbank.accountmaintenance.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.abcbank.accountmaintenance.entity.seqgenerator.StringSequenceIdentifier;
import com.abcbank.accountmaintenance.util.AppUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "transaction")

@Builder
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@JsonInclude(JsonInclude.Include.ALWAYS)
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Transaction implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transaction_number_gen_seq")
	@GenericGenerator(name = "transaction_number_gen_seq", strategy = "com.abcbank.accountmaintenance.entity.seqgenerator.StringSequenceIdentifier", parameters = {
			@Parameter(name = StringSequenceIdentifier.INCREMENT_PARAM, value = "1"),
			@Parameter(name = StringSequenceIdentifier.VALUE_PREFIX_PARAMETER, value = "TX_"),
			@Parameter(name = StringSequenceIdentifier.NUMBER_FORMAT_PARAMETER, value = "%010d") })

	@Column(name = "transaction_id")
	@EqualsAndHashCode.Include
	private String transactionId;
	
	@Column(name = "amount")
	private BigDecimal amount;

	@Column(name = "discriminator", nullable = false)
	private String discriminator;

	@Temporal(TemporalType.TIMESTAMP)
	@Builder.Default
	private Date transactionDate = new Date();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "account_number", updatable = false)
	private Account account;

	public String getAccountNumber() {
		return this.account.getAccountNumber();
	}

	public Date getTxDateWithoutTime() {
		return AppUtil.truncateDate(transactionDate);
	}
}