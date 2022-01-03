package com.vijay.shb.entity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * This is very demo table. In real world the table would have many more values and probably some kind of national ID to represent a person.  
 * @author Vijay
 *
 */
@Entity
public class Person {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@Column(name = "personnummer", unique = true)
	private final String personalNumber;
	
	@Column(name = "name")
	private final String name;
	
	@Column(name = "address")
	private final String address;
	
	@OneToMany(mappedBy = "person", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private final Set<PersonAccount>  accounts;
	
	@SuppressWarnings("unused")
	Person() {
	
		this.name = null;
		this.address = null;
		this.personalNumber = null;
		this.accounts = null;
	}
	
	public Person(final String personalNumber, final String name, final String address) {
		this.name = name;
		this.address = address;
		this.personalNumber = personalNumber;	
		this.accounts = new HashSet<>();
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public Set<PersonAccount> getAccounts() {
		return accounts;
	}

	public String getPersonalNumber() {
		return personalNumber;
	}
	
	
	@Override
	public int hashCode() {
		return Objects.hash(personalNumber);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Person other = (Person) obj;
		return Objects.equals(personalNumber, other.personalNumber);
	}

	@Override
	public String toString() {
		return "Person [personalNumber=" + personalNumber + ", name=" + name + ", address=" + address + "]";
	}		

}
