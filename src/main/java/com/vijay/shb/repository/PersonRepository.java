package com.vijay.shb.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.vijay.shb.entity.Person;

public interface PersonRepository extends JpaRepository<Person, Long> {
	
	Optional<Person> findByPersonalNumber(String personalNumber);
	

}
