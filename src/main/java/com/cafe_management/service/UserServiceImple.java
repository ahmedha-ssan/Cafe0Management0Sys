package com.cafe_management.service;

import com.cafe_management.API.APIUtil;
import com.cafe_management.API.ApiFillter;
import com.cafe_management.API.CustomerUserDetailsService;
import com.cafe_management.Model.User;
import com.cafe_management.constents.CafeConstants;
import com.cafe_management.dao.UserDao;
import com.cafe_management.utils.CafeUtils;
import com.cafe_management.utils.EmailUtils;
import com.cafe_management.weapper.UserWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;

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
    @Autowired
    ApiFillter apiFillter;
    @Autowired
    EmailUtils emailUtils;
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
            }
        }catch (Exception ex){
            log.info("{}", ex);
        }
        return CafeUtils.getResponseEntity(CafeConstants.Something_Went_Wrong,HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try {
            if (apiFillter.isAdmin()){
                return new ResponseEntity<>(userDao.getAllUser(),HttpStatus.OK);
            }else {
                return new ResponseEntity<>(new ArrayList<>(),HttpStatus.UNAUTHORIZED);
            }
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if (apiFillter.isAdmin()){
                Optional<User> optionalUser = userDao.findById(Integer.parseInt(requestMap.get("id")));
                if (!optionalUser.isEmpty()){
                    userDao.updateStatus(requestMap.get("status"),Integer.parseInt(requestMap.get("id")));
                    sendMailToAllAdmin(requestMap.get("status"),optionalUser.get().getEmail(),userDao.getAllAdmin());
                    return CafeUtils.getResponseEntity("User Status updated successfully",HttpStatus.OK);
                }else{
                    CafeUtils.getResponseEntity("User ID dose not exist",HttpStatus.OK);
                }
            }else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTH,HttpStatus.UNAUTHORIZED);
            }
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.Something_Went_Wrong,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
        allAdmin.remove(apiFillter.getCurrentUser());
        if (status!=null && status.equalsIgnoreCase("true")){
            emailUtils.sendMSG(apiFillter.getCurrentUser()
                    , "Account is disabled"
                    ,"User:- "+user+" \n is disabled by \nAdmin"+apiFillter.getCurrentUser()
                    ,allAdmin);
        }else {
            emailUtils.sendMSG(apiFillter.getCurrentUser()
                    , "Account is approved"
                    ,"User:- "+user+" \n is approved by \nAdmin"+apiFillter.getCurrentUser()
                    ,allAdmin);
        }
    }
}