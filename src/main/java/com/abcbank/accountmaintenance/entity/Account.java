package com.abcbank.accountmaintenance.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import com.abcbank.accountmaintenance.entity.seqgenerator.StringSequenceIdentifier;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "account")

@Builder
@JsonAutoDetect(fieldVisibility = Visibility.ANY)
@JsonInclude(JsonInclude.Include.ALWAYS)
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Account implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "account_number")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_number_gen_seq")
	@GenericGenerator(name = "account_number_gen_seq", strategy = "com.abcbank.accountmaintenance.entity.seqgenerator.StringSequenceIdentifier", parameters = {
			@Parameter(name = StringSequenceIdentifier.INCREMENT_PARAM, value = "1"),
			@Parameter(name = StringSequenceIdentifier.VALUE_PREFIX_PARAMETER, value = "ABC_"),
			@Parameter(name = StringSequenceIdentifier.NUMBER_FORMAT_PARAMETER, value = "%010d") })

	@EqualsAndHashCode.Include
	private String accountNumber;

	@Column(name = "account_holder")
	private String acountHolder;

	@Column(name = "balance", precision = 19, scale = 2, columnDefinition = "DECIMAL(19,2)")
	@Type(type = "java.math.BigDecimal")
	private BigDecimal balance;

	@Temporal(TemporalType.DATE)
	@Builder.Default
	private Date createdDate = new Date();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
	@org.hibernate.annotations.LazyCollection(org.hibernate.annotations.LazyCollectionOption.EXTRA)
	@JsonIgnore
	@ToString.Exclude
	private List<Transaction> transactions;

}
