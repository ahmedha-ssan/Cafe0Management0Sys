package com.cafe_management.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmailUtils {
    @Autowired
    private JavaMailSender emailSender;

    public void sendMSG(String to, String sub, String txt, List<String> list){
        SimpleMailMessage message= new SimpleMailMessage();
        message.setFrom("ahmedzfcai@gmail.com");
        message.setTo(to);
        message.setSubject(sub);
        message.setText(txt);
        if (list != null && list.size()>0)
        {
            message.setCc(getCCArray(list));
        }
        emailSender.send(message);
    }
    public String[] getCCArray(List<String> cclist){
        String[] cc =new String[cclist.size()];
        for (int i=0;i<cclist.size();i++){
            cc[i]=cclist.get(i);
        }
        return cc;
    }
}
