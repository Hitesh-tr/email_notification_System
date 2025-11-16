package com.emailvarification.v1.config;

import java.security.PrivateKey;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class EmailConfiguration {

    @Value("${spring.mail.username}")
    private String emailUserName;

    @Value("${spring.mail.password}")
    private String password;

    @Bean
    public JavaMailSender javaMailSender(){
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gamil.com");
        mailSender.setPort(587);
        mailSender.setUsername(emailUserName);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocal","smtp");
        props.put("mail.smtp.auth","true");
        props.put("mail.smtp.starttls","true");

        props.put("smtp.debug","true");

        return mailSender;
    }
}
