package com.cafe_management.dao;

import com.cafe_management.Model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryDao extends JpaRepository<Category,Integer> {
    List<Category> getAllCategory();
}
