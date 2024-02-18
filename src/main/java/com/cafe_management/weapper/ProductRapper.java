package com.cafe_management.weapper;

import lombok.Data;

@Data
public class ProductRapper {

    Integer id;
    String name;
    String description;
    Integer price;
    String status;
    Integer categoryId;
    String categoryName;

    public ProductRapper(){}
    public ProductRapper(Integer id,String name,String description,Integer price,String status,Integer categoryId,String categoryName){
        this.id=id;
        this.name=name;
        this.description=description;
        this.price=price;
        this.status=status;
        this.categoryId=categoryId;
        this.categoryName=categoryName;


    }
}
