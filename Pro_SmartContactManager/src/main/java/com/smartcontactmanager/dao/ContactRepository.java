package com.smartcontactmanager.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartcontactmanager.entities.Contact;
import com.smartcontactmanager.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	
	
//	@Query("from Contact as c where c.user.id = :userid")
//	public List<Contact> findContactsByUser(@Param("userid")int userid);
	
	
//	for pagination
	
	//currentpage - page
	//contact per page - 5
	
	@Query("from Contact as c where c.user.id = :userid")
	public Page<Contact> findContactsByUser(@Param("userid")int userid, Pageable pageable );
	
	//Search
	public List<Contact> findByNameContainingAndUser(String name, User user);
	
}
