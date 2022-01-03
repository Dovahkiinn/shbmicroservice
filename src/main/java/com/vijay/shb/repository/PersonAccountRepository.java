package com.vijay.shb.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.vijay.shb.entity.PersonAccount;

public interface PersonAccountRepository extends JpaRepository<PersonAccount, Long>{

	@Query("SELECT pa from PersonAccount pa where pa.person.personalNumber =:personnummer ")   
    List<PersonAccount> findByPersonNummer(@Param("personnummer") String personnummer);
	
}
