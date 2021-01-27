package com.sawoo.pipeline.api.service.infra.email;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.EmailException;
import com.sawoo.pipeline.api.dto.email.EmailDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.mail.internet.MimeMessage;
import javax.validation.ConstraintViolationException;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.containsString;
import static org.awaitility.Awaitility.await;

/**
 * This test requires GreenMail server to running in the resting environment
 * Best way I've found so far is using docker:
 * Step 1: docker pull greenmail/standalone:1.6.1
 * Step 2: docker run -t -i -p 3025:3025 -p 3110:3110 -p 3143:3143 \
 *                  -p 3465:3465 -p 3993:3993 -p 3995:3995 -p 8080:8080 \
 *                  greenmail/standalone:1.6.1
 *
 *  Check out https://greenmail-mail-test.github.io/greenmail/#deploy_docker_standalone for more details
 */

@Testcontainers
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Tags({@Tag(value = "integration")})
@Profile(value = {"unit-tests", "unit-tests-embedded"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("greenpapa", "greenfield"))
            .withPerMethodLifecycle(false);

    @Test
    void sendWhenEmailEntityIsValidReturnSuccess() {
        // Assign
        String TO = "miguel.maquieira@gmail.com";
        String SUBJECT = "GreenMail Test";
        String CONTENT = "Spring Mail Integration Testing with JUnit and GreenMail Example";
        int NUMBER_OF_RECIPIENTS = 1;
        EmailDTO mail = EmailDTO
                .builder()
                .to(TO)
                .subject(SUBJECT)
                .message(CONTENT)
                .build();

        // Act
        emailService.send(mail);

        // Assert
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];
            Assertions.assertAll("Assert email message is correctly sent",
                    () -> Assertions.assertEquals(
                            CONTENT,
                            GreenMailUtil.getBody(receivedMessage),
                            String.format("Email content must be [%s]", CONTENT)),
                    () -> Assertions.assertEquals(
                            NUMBER_OF_RECIPIENTS,
                            receivedMessage.getAllRecipients().length,
                            String.format("Number of recipients must be [%d]", NUMBER_OF_RECIPIENTS)),
                    () -> Assertions.assertEquals(
                            TO,
                            receivedMessage.getAllRecipients()[0].toString(),
                            String.format("Recipient must be [%s]", TO)));
        });
    }

    @Test
    void sendWhenEmailEntityNotValidSubjectNullReturnFailure() {
        // Assign
        String TO = "miguel.maquieira@gmail.com";
        String CONTENT = "Spring Mail Integration Testing with JUnit and GreenMail Example";
        EmailDTO mail = EmailDTO
                .builder()
                .to(TO)
                .message(CONTENT)
                .build();

        // Act / Assert
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () ->  emailService.send(mail),
                "send must throw a ConstraintViolationException");
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
                        .matches(exception.getMessage()));
        Assertions.assertEquals(1, exception.getConstraintViolations().size());
    }

    @Test
    void sendWhenEmailEntityNotValidToNullReturnFailure() {
        // Assign
        String CONTENT = "Spring Mail Integration Testing with JUnit and GreenMail Example";
        String SUBJECT = "GreenMail Test";
        EmailDTO mail = EmailDTO.
                builder()
                .subject(SUBJECT)
                .message(CONTENT)
                .build();

        // Act / Assert
        EmailException exception = Assertions.assertThrows(
                EmailException.class,
                () ->  emailService.send(mail),
                "send must throw a EmailException");
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR)
                        .matches(exception.getMessage()));
    }

    @Test
    void sendWhenEmailEntityNotValidToEmptyReturnFailure() {
        // Assign
        String CONTENT = "Spring Mail Integration Testing with JUnit and GreenMail Example";
        String SUBJECT = "GreenMail Test";
        EmailDTO mail = new EmailDTO();
        mail.setSubject(SUBJECT);
        mail.setMessage(CONTENT);
        mail.setTo("");

        // Act / Assert
        EmailException exception = Assertions.assertThrows(
                EmailException.class,
                () ->  emailService.send(mail),
                "send must throw a EmailException");
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR)
                        .matches(exception.getMessage()));
    }

    @Test
    void sendWhenEmailEntityNotValidToWrognEmailReturnFailure() {
        // Assign
        String CONTENT = "Spring Mail Integration Testing with JUnit and GreenMail Example";
        String SUBJECT = "GreenMail Test";
        String TO = "wrongEmail";
        EmailDTO mail = EmailDTO
                .builder()
                .to(TO)
                .subject(SUBJECT)
                .message(CONTENT)
                .build();

        // Act / Assert
        ConstraintViolationException exception = Assertions.assertThrows(
                ConstraintViolationException.class,
                () ->  emailService.send(mail),
                "send must throw a ConstraintViolationException");
        Assertions.assertTrue(
                containsString(ExceptionMessageConstants.COMMON_FIELD_MUST_BE_AN_EMAIL_ERROR)
                        .matches(exception.getMessage()));
        Assertions.assertEquals(1, exception.getConstraintViolations().size());
    }

    @Test
    void sendWithAttachmentWhenEmailEntityIsValidReturnSuccess() {
        // Assign
        /*String TO = "miguel.maquieira@gmail.com";
        String SUBJECT = "GreenMail Test";
        String CONTENT = "Spring Mail Integration Testing with JUnit and GreenMail Example";
        int NUMBER_OF_RECIPIENTS = 1;
        EmailWithAttachmentDTO mail = EmailWithAttachmentDTO
                .builder()
                .to(TO)
                .subject(SUBJECT)
                .message(CONTENT)
                .build();

        // Act
        emailService.sendWithAttachment(mail);

        // Assert
        MimeMessage receivedMessage = greenMail.getReceivedMessages()[0];
        Assertions.assertAll("Assert email message is correctly sent",
                () -> Assertions.assertEquals(
                        CONTENT,
                        GreenMailUtil.getBody(receivedMessage),
                        String.format("Email content must be [%s]", CONTENT)),
                () -> Assertions.assertEquals(
                        NUMBER_OF_RECIPIENTS,
                        receivedMessage.getAllRecipients().length,
                        String.format("Number of recipients must be [%d]", NUMBER_OF_RECIPIENTS)),
                () -> Assertions.assertEquals(
                        TO,
                        receivedMessage.getAllRecipients()[0].toString(),
                        String.format("Recipient must be [%s]", TO)));*/
    }
}
