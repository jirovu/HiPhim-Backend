package com.web.hiphim.services.mail;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Data
@AllArgsConstructor
public class MailProvider {
    @Value("${spring.mail.host}")
    private String mailHost;
    @Value("${spring.mail.port}")
    private int mailPort;
    @Value("${spring.mail.username}")
    private String username;
    @Value("${spring.mail.password}")
    private String password;
    @Autowired
    private JavaMailSender mailSender;
    private SimpleMailMessage simpleMailMessage;

    public MailProvider() {
        simpleMailMessage = new SimpleMailMessage();
    }

    public boolean sendMailTo(String mail, String identifyCode) {
        try {
            simpleMailMessage.setTo(mail);
            simpleMailMessage.setSubject("HIPHIM XIN CHÀO - ĐẶT LẠI MẬT KHẨU CỦA BẠN");
            simpleMailMessage.setText("- CODE : " + identifyCode);

            mailSender.send(simpleMailMessage);
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
