package com.cafe_management.service;

import com.cafe_management.weapper.ProductRapper;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface ProductService {

    ResponseEntity<String> addNewProduct(Map<String,String>requestMap);
    ResponseEntity<List<ProductRapper>> getAllProduct();

    ResponseEntity<String> updateProduct(Map<String,String>requestMap);

    ResponseEntity<String> deleteProduct(Integer id);

    ResponseEntity<String> updateStatus(Map<String,String>requestMap);

    ResponseEntity<List<ProductRapper>> getByCategory(Integer id);
    ResponseEntity<ProductRapper> getProductById(Integer id);



}
