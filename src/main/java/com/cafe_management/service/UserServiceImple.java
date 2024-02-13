package com.cafe_management.service;

import com.cafe_management.API.APIUtil;
import com.cafe_management.API.ApiFillter;
import com.cafe_management.API.CustomerUserDetailsService;
import com.cafe_management.Model.User;
import com.cafe_management.constents.CafeConstants;
import com.cafe_management.dao.UserDao;
import com.cafe_management.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class UserServiceImple implements UserService{
    @Autowired
    UserDao userDao;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    CustomerUserDetailsService customerUserDetailsService;
    @Autowired
    APIUtil apiUtil;
    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {

        log.info("Inside Signup{}",requestMap);
        try {
            if (validateSignUpMap(requestMap)){
                User user = userDao.findBYEmailId(requestMap.get("email"));
                if(Objects.isNull(user)){
                    userDao.save(getUserFromMap(requestMap));
                    return CafeUtils.getResponseEntity("Successfully Registered.",HttpStatus.OK);
                }
                else {
                    return CafeUtils.getResponseEntity("Email already exist",HttpStatus.BAD_REQUEST);
                }
            }
            else {
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        }catch (Exception ex){
            log.info("{}", ex);
        }
       return CafeUtils.getResponseEntity(CafeConstants.Something_Went_Wrong,HttpStatus.INTERNAL_SERVER_ERROR);
    }



    private boolean validateSignUpMap(Map<String,String> requestMap){
        if (requestMap.containsKey("name")&& requestMap.containsKey("contactNumber")&&requestMap.containsKey("email")&&requestMap.containsKey("password")) {
            return true;
        }
        else {
            return false;
        }
    }

    private User getUserFromMap(Map<String,String> requestMap){
        User user =new User();
        user.setName(requestMap.get("name"));
        user.setContactNumber(requestMap.get("contactNumber"));
        user.setEmail(requestMap.get("email"));
        user.setPassword(requestMap.get("password"));
        user.setStatus("false");
        user.setRole("user");
        return user;
    }


    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("Inside login");
        try {
            Authentication authentication= authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"),requestMap.get("password")));
            if (authentication.isAuthenticated()){
                System.out.println("Ssssssssssssssssssssssssssssssssssss");
                User user = userDao.findBYEmailId(requestMap.get("email"));
                if (user.getStatus().equalsIgnoreCase("true")) {
                    return new ResponseEntity<String>("{\"token\":\"" + apiUtil.generateToken(user.getEmail(), user.getRole()) + "\"}", HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>("{\"message\":\"Wait For Admin Approval\"}", HttpStatus.BAD_REQUEST);
                }
//                if (customerUserDetailsService.getUserDetails().getStatus().equalsIgnoreCase("true")){
//                    return new ResponseEntity<String>("{\"token\":\""+apiUtil
//                            .generateToken(customerUserDetailsService.getUserDetails().getEmail()
//                                    ,customerUserDetailsService.getUserDetails().getRole()) +"\"}"
//                            ,HttpStatus.OK);
//                }
//                else {
//                    System.out.println("zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz");
//
//                    return new ResponseEntity<String>("{\"message\":\""+"wait for admin approval."+"\"}",HttpStatus.BAD_REQUEST);
//                }
            }

        }catch (Exception ex){
            log.info("{}", ex);
        }
        return CafeUtils.getResponseEntity(CafeConstants.Something_Went_Wrong,HttpStatus.BAD_REQUEST);
    }
}
