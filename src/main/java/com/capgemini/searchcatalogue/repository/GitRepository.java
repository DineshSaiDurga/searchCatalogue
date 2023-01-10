package com.capgemini.searchcatalogue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.capgemini.searchcatalogue.model.GitDetails;

import jakarta.transaction.Transactional;


@Transactional
@Repository
public interface GitRepository extends JpaRepository<GitDetails, Long> {

}
