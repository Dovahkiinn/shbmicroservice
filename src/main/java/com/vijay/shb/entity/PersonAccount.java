package com.vijay.shb.entity;

import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.vijay.shb.model.AccountType;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "ACCOUNT_NUMBER","PERSON_NUMMER", "ACCOUNT_TYPE" }) })
public class PersonAccount {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name = "ACCOUNT_NUMBER")
	private final long accountNumber;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSON_NUMMER", nullable = false)
	private final Person person;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "ACCOUNT_TYPE")
	private final AccountType accountType;
	
	private final BigDecimal amount;
	
	@SuppressWarnings("unused")
	PersonAccount() {
		this.accountType = null;
		this.person = null;
		this.accountNumber = 0;
		this.amount = null;
	}	
	
	public PersonAccount(final Person person, final long accountNumber, 
			final AccountType accountType, final BigDecimal amount) {	
		this.person = person;
		this.accountType = accountType;
		this.accountNumber = accountNumber;
		this.amount = amount;
	}

	public Person getPerson() {
		return person;
	}
	
	public long getId() {
		return id;
	}


	//TODO: Verify with customer what exactly they need as account number. Unique, for sure but may be not autopgenerated ID as it is implemented now. 
	public long getAccountNumber() {
		return getId();
	}
	
	public AccountType getAccountType() {
		return accountType;
	}	

	public BigDecimal getAmount() {
		return amount;
	}	

	@Override
	public int hashCode() {
		return Objects.hash(accountNumber, accountType, person);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersonAccount other = (PersonAccount) obj;
		return accountNumber == other.accountNumber && accountType == other.accountType
				&& Objects.equals(person.getPersonalNumber(), other.person.getPersonalNumber());
	}

	@Override
	public String toString() {
		return "PersonAccount [accountNumber=" + accountNumber + 
				", person=" + person + 
				", accountType=" + accountType + ", amount=" + amount + "]";
	}		
	
}
