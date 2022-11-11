package com.VinayShenoy.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.VinayShenoy.entity.User;

public interface UserRepository extends JpaRepository<User,Long>{

	@Query("select u from User u where u.email = :email")
	public User getUserByUserName(@Param("email") String email);
	
	
}
