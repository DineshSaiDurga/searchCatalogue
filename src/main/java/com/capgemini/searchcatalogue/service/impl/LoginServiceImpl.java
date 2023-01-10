package com.capgemini.searchcatalogue.service.impl;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.capgemini.searchcatalogue.model.UserDetails;
import com.capgemini.searchcatalogue.repository.UserRepository;
import com.capgemini.searchcatalogue.service.LoginService;

@Service
public class LoginServiceImpl implements LoginService {

	@Autowired
	UserRepository userrepo;
	
	@Override
	public ResponseEntity<UserDetails> validateUserCredentials(String emailId, String password) {
		String encodedpassword= Base64.getEncoder().encodeToString(password.getBytes());
		UserDetails lucideuser=userrepo.findByEmailId(emailId);
		if(lucideuser!=null) {
			if(encodedpassword.equals(lucideuser.getPassword())) {
				return new ResponseEntity<UserDetails>(lucideuser,HttpStatus.OK);
			}else {
				return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
			}
		}else {
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

}
