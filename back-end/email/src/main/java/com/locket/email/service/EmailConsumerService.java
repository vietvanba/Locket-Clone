package com.locket.email.service;

import com.locket.email.constant.KafkaTopic;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static com.locket.email.constant.EmailType.FORGOT_PASSWORD;
import static com.locket.email.constant.EmailType.VERIFY_EMAIL;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailConsumerService {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    @Value("${service.profile.url}")
    private String profileServiceUrl;
    @Value("${spring.mail.username}")
    private String mailUsername;
    @KafkaListener(topics = KafkaTopic.EMAIL_SENDER_TOPIC, groupId = "${spring.kafka.consumer.group-id}")
    public void listenEmail(ConsumerRecord<GenericRecord, GenericRecord> record) {
        GenericRecord key = record.key();
        GenericRecord value = record.value();
        switch (value.get("type").toString()) {
            case VERIFY_EMAIL: {
                Map<String, Object> data = new HashMap<>();
                data.put("name", value.get("name").toString());
                data.put("confirmationLink", String.format("%s/confirm?userId=%s&token=%s", profileServiceUrl, key.get("userId"), value.get("token")));
                sendEmail(key.get("email").toString(), "Verify email", "registry", data);
                break;
            }
            case FORGOT_PASSWORD:{
                Map<String, Object> data = new HashMap<>();
                data.put("name", value.get("name").toString());
                data.put("confirmationLink", String.format("%s/forgot-password?userId=%s&token=%s", profileServiceUrl, key.get("userId"), value.get("token")));
                sendEmail(key.get("email").toString(), "Forgot password", "forgot-password", data);
                break;
            }
            default: {
                throw new RuntimeException("Somethings went wrong");
            }
        }
    }

    private void sendEmail(String to, String subject, String templateName, Map<String, Object> data) {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
        try {
            helper.setFrom(mailUsername);
            helper.setTo(to);
            helper.setSubject(subject);
            Context context = new Context();
            context.setVariables(data);
            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
