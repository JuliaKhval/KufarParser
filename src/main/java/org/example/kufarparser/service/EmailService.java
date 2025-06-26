package org.example.kufarparser.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.example.kufarparser.model.Apartment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


import java.util.List;
@Service
@RequiredArgsConstructor
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private final JavaMailSender mailSender;
    private final ObjectMapper objectMapper;


    private static final String FROM_EMAIL = "juliakhval2512@gmail.com";
    private static final String ADMIN_EMAIL = "khvalangel@gmail.com";

    public void sendJsonEmail(List<Apartment> apartments) {
        if (apartments == null || apartments.isEmpty()) {
            logger.warn("Попытка отправить пустой список квартир");
            return;
        }

        try {
            logger.debug("Подготовка email с {} квартирами", apartments.size());

            String jsonContent = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(apartments);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(FROM_EMAIL);
            helper.setTo(ADMIN_EMAIL);
            helper.setSubject("Новые объявления квартир | " + apartments.size() + " шт.");


            helper.setText("Во вложении список новых квартир", false);

            helper.addAttachment("apartments.json",
                    new ByteArrayResource(jsonContent.getBytes()));

            logger.debug("Отправка письма...");
            mailSender.send(message);
            logger.info("Письмо успешно отправлено");

        } catch (MailAuthenticationException e) {
            logger.error("Ошибка аутентификации: {}", e.getMessage());
            throw new RuntimeException("Проверьте логин/пароль в application.properties", e);
        } catch (Exception e) {
            logger.error("Ошибка отправки: {}", e.getMessage());
            throw new RuntimeException("Не удалось отправить письмо", e);
        }
    }
}