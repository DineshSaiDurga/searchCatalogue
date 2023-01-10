package com.capgemini.searchcatalogue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capgemini.searchcatalogue.model.UserDetails;

import jakarta.transaction.Transactional;

@Transactional
@Repository
public interface UserRepository extends JpaRepository<UserDetails, Long> {

	UserDetails findByEmailId(String emailId);

}
