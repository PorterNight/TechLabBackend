package techlab.backend.service.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    private final JavaMailSender emailSender;

    @Value("${techlab.domain}")
    private String domain;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendUserEmailConfirmation(String to, String confirmationToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@techlab.com");
        message.setTo(to);
        message.setSubject("Email Confirmation");
        message.setText("To confirm your email, please click the link below:\n"
                + domain + "/confirm-email?token=" + confirmationToken);
        try {
            emailSender.send(message);
        } catch (MailException e) {
            log.warn(e.getMessage());
        }

    }
}