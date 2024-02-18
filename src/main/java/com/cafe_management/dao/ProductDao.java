package com.cafe_management.dao;

import com.cafe_management.Model.Product;
import com.cafe_management.weapper.ProductRapper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductDao extends JpaRepository<Product,Integer> {

    List<ProductRapper> getAllProduct();

}
