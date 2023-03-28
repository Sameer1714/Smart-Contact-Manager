package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;

@RestController
public class SearchController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	ContactRepository contactRepository;
	
	//Search Handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> Search(@PathVariable("query") String query,Principal principal){
		
		System.out.println("Query : "+query);
		
		System.out.println("user name  : "+principal.getName());
		
		User user = userRepository.findByEmail(principal.getName());
		
		List<Contact> contact = contactRepository.findByNameContainingAndUser(query, user);
		System.out.println(contact);
		
		return ResponseEntity.ok(contact);
	}
}
