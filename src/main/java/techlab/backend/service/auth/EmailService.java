package techlab.backend.service.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender emailSender;
    
    @Value("${techlab.domain}")
    private String domain;

    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendUserRegisterConfirmationEmail(String to, String confirmationToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply@example.com");
        message.setTo(to);
        message.setSubject("Email Confirmation");
        message.setText("To confirm your email, please click the link below:\n"
                + domain + "/confirm?token=" + confirmationToken);
        emailSender.send(message);
    }
}