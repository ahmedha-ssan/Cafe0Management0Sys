package com.cafe_management.rest;

import com.cafe_management.constents.CafeConstants;
import com.cafe_management.service.BillService;
import com.cafe_management.utils.CafeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class BillRestImp implements BillRest{
    @Autowired
    BillService billService;

    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        try {
            return billService.generateReport(requestMap);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.Something_Went_Wrong, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
