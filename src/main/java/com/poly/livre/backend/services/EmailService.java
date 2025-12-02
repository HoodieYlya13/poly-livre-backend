package com.poly.livre.backend.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendMagicLink(String to, String link) {
        log.info("Sending magic link to {}: {}", to, link);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject("Your Magic Link Login");
            message.setText("Click the link to login: " + link);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email (check SMTP config): {}", e.getMessage());
        }
    }

}