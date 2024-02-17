package com.cafe_management.service;

import com.cafe_management.API.ApiFillter;
import com.cafe_management.Model.Category;
import com.cafe_management.constents.CafeConstants;
import com.cafe_management.dao.CategoryDao;
import com.cafe_management.utils.CafeUtils;
import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CategoryServiceImp implements CategoryService{
    @Autowired
    CategoryDao categoryDao;
    @Autowired
    ApiFillter apiFillter;
    @Override
    public ResponseEntity<String> addNewCategory(Map<String, String> requestMap) {
        try {
            if (apiFillter.isAdmin()){
                if (validateCategoryMap(requestMap,false)){
                    categoryDao.save(getCategoryFromMap(requestMap,false));
                    System.out.printf("55555555555555");
                    return CafeUtils.getResponseEntity("Category Added successfully",HttpStatus.OK);
                }
            }
            else{
                System.out.printf("555555555555551");
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTH, HttpStatus.UNAUTHORIZED);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        System.out.printf("555555555555552");
        return CafeUtils.getResponseEntity(CafeConstants.Something_Went_Wrong, HttpStatus.INTERNAL_SERVER_ERROR);

    }




    private boolean validateCategoryMap(Map<String, String> requestMap, boolean validateId) {
        if (requestMap.containsKey("name")) {
            if (requestMap.containsKey("id") && validateId) {
                return true;
            } else if (!validateId){
                return true;
            }
        }
        return false;
    }

    private Category getCategoryFromMap(Map<String,String> requestMap,boolean isAdd){
        Category category=new Category();
        if(isAdd){
            category.setId(Integer.parseInt(requestMap.get("id")));
        }
        category.setName(requestMap.get("name"));
        return category;
    }
    @Override
    public ResponseEntity<List<Category>> getAllCategory(String filterValue) {
        try {
            if (!Strings.isNullOrEmpty(filterValue) && filterValue.equalsIgnoreCase("true")){
                return new ResponseEntity<List<Category>>(categoryDao.getAllCategory(),HttpStatus.OK);
            }
            return new ResponseEntity<>(categoryDao.findAll(),HttpStatus.OK);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<List<Category>>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @Override
    public ResponseEntity<String> updateCategory(Map<String, String> requestMap) {
        try {
            if (apiFillter.isAdmin()){
                if (validateCategoryMap(requestMap,true)){
                    Optional optional = categoryDao.findById(Integer.parseInt(requestMap.get("id")));
                    if (!optional.isEmpty()){
                        categoryDao.save(getCategoryFromMap(requestMap,true));
                        return CafeUtils.getResponseEntity("Category updated successfully", HttpStatus.OK);
                    }else {
                        return CafeUtils.getResponseEntity("Category id dose not exist", HttpStatus.OK);
                    }
                }
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);

            }
            else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTH, HttpStatus.UNAUTHORIZED);

            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.Something_Went_Wrong, HttpStatus.INTERNAL_SERVER_ERROR);

    }
}
