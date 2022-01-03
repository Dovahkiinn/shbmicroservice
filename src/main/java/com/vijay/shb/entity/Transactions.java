package com.vijay.shb.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Immutable;

//TODO: Starting point for Bonusuppgift->
//					Endpoints:
//							* hämta transaktioner till ett specifikt konto
//							* filtrera transaktioner på fromDate och toDate

@Entity
@Immutable
public class Transactions {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	private final LocalDateTime transactionTime;

	private final long transactionAmount;

	Transactions() {

		this.transactionTime = null;
		this.transactionAmount = 0l;
		this.account = null;
	}

	public Transactions(LocalDateTime transactionTime, long transactionAmount, PersonAccount account) {

		this.transactionTime = transactionTime;
		this.transactionAmount = transactionAmount;
		this.account = account;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public PersonAccount getAccount() {
		return account;
	}

	public void setAccount(PersonAccount account) {
		this.account = account;
	}

	public LocalDateTime getTransactionTime() {
		return transactionTime;
	}

	public long getTransactionAmount() {
		return transactionAmount;
	}

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private PersonAccount account;

}
