package com.bookreader.app.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookreader.app.entity.User;


public interface UserJPARepository  extends JpaRepository<User, Long>{
	
	List<User> findByUserName(String username);

}
