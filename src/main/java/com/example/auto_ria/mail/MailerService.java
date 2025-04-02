package com.example.auto_ria.mail;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.example.auto_ria.configurations.MailConfiguration;
import com.example.auto_ria.enums.EMail;
import com.example.auto_ria.enums.ERole;
import com.example.auto_ria.exceptions.email.EmailSendFailedException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MailerService {

    private JavaMailSender javaMailSender;
    private Configuration freemarkerConfig;
    private MailConfiguration mailConfig;
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void handleEmail(Runnable emailTask) {
        scheduler.execute(() -> retryEmail(emailTask, 3, 5, 0));
    }

    private void retryEmail(Runnable emailTask, int maxRetries, int delaySeconds, int attempt) {
        try {
            emailTask.run();
        } catch (Exception e) {
            int nextAttempt = attempt + 1;
            scheduler.schedule(() -> retryEmail(emailTask, maxRetries, delaySeconds, nextAttempt), delaySeconds,
                    TimeUnit.SECONDS);
        }
    }

    public void sendWelcomeEmail(String userName, String userEmail) {
        Map<String, String> map = new HashMap<>();
        map.put("name", userName);

        handleEmail(() -> sendEmail(userEmail, EMail.WELCOME, map));
    }

    public void sendRegisterInviteEmail(String userEmail, ERole role, String code) {
        Map<String, String> map = new HashMap<>();
        map.put("code", code);
        map.put("role", role.name());

        handleEmail(() -> sendEmail(userEmail, EMail.REGISTER, map));
    }

    public void sendActivationKeyEmail(String userEmail, String code) {
        Map<String, String> map = new HashMap<>();
        map.put("code", code);

        handleEmail(() -> sendEmail(userEmail, EMail.REGISTER_KEY, map));
    }

    public void sendPlatformLeaveEmail(String userEmail, String userName) {
        Map<String, String> map = new HashMap<>();
        map.put("register_url", "some frontend url");
        map.put("name", userName);
        map.put("email", userEmail);

        handleEmail(() -> sendEmail(userEmail, EMail.PLATFORM_LEAVE, map));
    }

    public void sendAccountBannedEmail(String userEmail, String userName) {
        Map<String, String> map = new HashMap<>();
        map.put("name", userName);
        map.put("email", userEmail);

        handleEmail(() -> sendEmail(userEmail, EMail.YOUR_ACCOUNT_BANNED, map));
    }

    public void sendCarBeingCheckedEmail(String userEmail, String userName, String filteredDescription) {
        Map<String, String> map = new HashMap<>();
        map.put("name", userName);
        map.put("description", filteredDescription);

        handleEmail(() -> sendEmail(userEmail, EMail.CAR_BEING_CHECKED, map));
    }

    public void sendCarBannedEmail(String userEmail, String userName) {
        Map<String, String> map = new HashMap<>();
        map.put("name", userName);

        handleEmail(() -> sendEmail(userEmail, EMail.CAR_BEING_BANNED, map));
    }

    public void sendCarActivatedEmail(String userEmail, String userName, int carId) {
        Map<String, String> map = new HashMap<>();
        map.put("name", userName);
        map.put("car_id", String.valueOf(carId));

        handleEmail(() -> sendEmail(userEmail, EMail.CAR_BEING_ACTIVATED, map));
    }

    public void sendCheckAnnouncementEmail(String userEmail, String unfilteredDescription) {
        Map<String, String> map = new HashMap<>();
        map.put("description", unfilteredDescription);

        handleEmail(() -> sendEmail(userEmail, EMail.CHECK_ANNOUNCEMENT, map));
    }

    public void sendPremiumBoughtEmail(String userEmail, String userName) {
        Map<String, String> map = new HashMap<>();
        map.put("name", userName);

        handleEmail(() -> sendEmail(userEmail, EMail.PREMIUM_BOUGHT, map));
    }

    public void sendPremiumContinueFailedEmail(String userEmail, String userName, LocalDateTime paymentAttemptDate) {
        Map<String, String> map = new HashMap<>();
        map.put("name", userName);
        map.put("payment_attempt_date", paymentAttemptDate.toString());

        handleEmail(() -> sendEmail(userEmail, EMail.PREMIUM_CONTINUE_FAILED, map));
    }

    public void sendPremiumContinueEmail(String userEmail, String userName, LocalDate nextBillingDate, LocalDateTime paymentDate) {
        Map<String, String> map = new HashMap<>();
        map.put("name", userName);
        map.put("renewal_date", paymentDate.toString());
        map.put("next_billing_date", nextBillingDate.toString());

        handleEmail(() -> sendEmail(userEmail, EMail.PREMIUM_RENEW, map));
    }

    public void sendPremiumStartFailedEmail(String userEmail, String userName, LocalDate paymentAttemptDate) {
        Map<String, String> map = new HashMap<>();
        map.put("name", userName);
        map.put("payment_attempt_date", paymentAttemptDate.toString());

        handleEmail(() -> sendEmail(userEmail, EMail.PREMIUM_START_FAIL, map));
    }

    public void sendPremiumCanceledEmail(String userEmail, String userName, LocalDateTime cancelDate) {
        Map<String, String> map = new HashMap<>();
        map.put("name", userName);
        map.put("cancellation_date", cancelDate.toString());

        handleEmail(() -> sendEmail(userEmail, EMail.PREMIUM_CANCELED, map));
    }

    public void sendForgotPasswordEmail(String userEmail, String code) {
        Map<String, String> map = new HashMap<>();
        map.put("code", code);
        map.put("email", userEmail);
        map.put("time", LocalDateTime.now().toString());

        handleEmail(() -> sendEmail(userEmail, EMail.FORGOT_PASSWORD, map));
    }

    private void sendEmail(String recipientEmail, EMail templateName, Map<String, String> variables) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            helper.setTo(recipientEmail);

            HashMap<String, String> result = mailConfig.parser(templateName);

            helper.setSubject(result.get("subject"));

            Template template = freemarkerConfig.getTemplate(result.get("templateName"));
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, variables);

            helper.setText(html, true);

            javaMailSender.send(message);
        } catch (Exception e) {
            throw new EmailSendFailedException("Email not send");
        }
    }
}
