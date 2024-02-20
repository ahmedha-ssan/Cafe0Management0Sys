package com.cafe_management.dao;

import com.cafe_management.Model.Product;
import com.cafe_management.weapper.ProductRapper;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductDao extends JpaRepository<Product,Integer> {

    List<ProductRapper> getAllProduct();

    @Modifying
    @Transactional
    Integer updateProductStatus(@Param("status") String status, @Param("id") Integer id);

    List<ProductRapper> getProductByCategory(@Param("id") Integer id);

    ProductRapper getProductById(@Param("id") Integer id);


}
