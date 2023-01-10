package com.capgemini.searchcatalogue.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.capgemini.searchcatalogue.model.UserDetails;

@Component
public interface LoginService {

	ResponseEntity<UserDetails> validateUserCredentials(String emailId, String password);

}
