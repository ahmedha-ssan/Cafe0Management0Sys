package com.cafe_management.dao;

import com.cafe_management.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UserDao extends JpaRepository<User,Integer> {
    User findBYEmailId(@Param("email") String email);
}
