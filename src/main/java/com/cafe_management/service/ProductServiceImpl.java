package com.cafe_management.service;

import com.cafe_management.API.ApiFillter;
import com.cafe_management.Model.Category;
import com.cafe_management.Model.Product;
import com.cafe_management.constents.CafeConstants;
import com.cafe_management.dao.ProductDao;
import com.cafe_management.utils.CafeUtils;
import com.cafe_management.weapper.ProductRapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService{
    @Autowired
    ApiFillter apiFillter;
    @Autowired
    ProductDao productDao;
    
    
    @Override
    public ResponseEntity<String> addNewProduct(Map<String, String> requestMap) {
        try {
            if (apiFillter.isAdmin()){
                if (validateProductMap(requestMap,false)){
                    productDao.save(getProductFromMap(requestMap,false));
                    return CafeUtils.getResponseEntity("Product added successfully",HttpStatus.OK);
                }
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA,HttpStatus.BAD_REQUEST);
            }else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTH,HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();;
        }
        return CafeUtils.getResponseEntity(CafeConstants.Something_Went_Wrong, HttpStatus.INTERNAL_SERVER_ERROR);
    }



    private boolean validateProductMap(Map<String, String> requestMap, boolean validateID) {
        if (requestMap.containsKey("name")){
            if (requestMap.containsKey("id") && validateID){
                return true;
            }
            else if (!validateID){
                return true;
            }
        }
        return false;
    }

    private Product getProductFromMap(Map<String, String> requestMap, boolean isAdd) {
        Product product =new Product();
        Category category=new Category();
        category.setId(Integer.parseInt(requestMap.get("categoryID")));


        if (isAdd){
            product.setId(Integer.parseInt(requestMap.get("id")));
        }else {
            product.setStatus("true");
        }
        product.setCategory(category);
        product.setName(requestMap.get("name"));
        product.setDescrition(requestMap.get("description"));
        product.setPrice(Integer.parseInt(requestMap.get("price")));
        return product;
    }

    @Override
    public ResponseEntity<List<ProductRapper>> getAllProduct() {
        try{
            return new ResponseEntity<>(productDao.getAllProduct(),HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();;
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<String> updateProduct(Map<String, String> requestMap) {
        try{

            if (apiFillter.isAdmin()){
                if (validateProductMap(requestMap,true)){
                    Optional<Product> optional = productDao.findById(Integer.parseInt(requestMap.get("id")));
                    if (!optional.isEmpty()){
                        Product product =getProductFromMap(requestMap,true);
                        product.setStatus(optional.get().getStatus());
                        productDao.save(product);
                        return CafeUtils.getResponseEntity("Product updated successfully",HttpStatus.OK);
                    }else {
                        return CafeUtils.getResponseEntity("Product id dose not exist.",HttpStatus.OK);
                    }
                }else {
                    return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA,HttpStatus.BAD_REQUEST);
                }
            }else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTH,HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();;
        }
        return CafeUtils.getResponseEntity(CafeConstants.Something_Went_Wrong,HttpStatus.INTERNAL_SERVER_ERROR);

    }

}
