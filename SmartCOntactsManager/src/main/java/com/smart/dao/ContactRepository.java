package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface ContactRepository extends JpaRepository<Contact,Integer> {

	//pageble contain current page and end page
	public Page<Contact> findByUser(User user,Pageable   pageable);
	
	public List<Contact> findByNameContainingAndUser(String name,User user);
	
	  public    List<Contact> findByName(String name);
}
