package com.sawoo.pipeline.api.service.infra.email;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.EmailException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import javax.validation.constraints.NotBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.info-account}")
    private String infoEmail;

    @Override
    public void send(@NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String to,
                     @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)String subject,
                     @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String message)
            throws EmailException {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        log.debug("Sending email to: {} subject: {}", to, subject);

        try {
            mailSender.send(mailMessage);
        } catch (MailException err) {
            throw new EmailException(
                    ExceptionMessageConstants.MAIL_EXCEPTION_SEND_MESSAGE,
                    new Object[] {to, subject, err.getCause()});
        }
    }

    @Override
    public void sendWithAttachment(
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String to,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String subject,
            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)String message,
            String filename, String fileType, String fileContent) throws EmailException {
        MimeMessage msg = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, CharEncoding.UTF_8);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(message, false);
            helper.addAttachment(filename,
                    new ByteArrayDataSource(fileContent.getBytes(), fileType)
            );
            mailSender.send(msg);
        } catch (MessagingException err) {
            throw new EmailException(
                    ExceptionMessageConstants.MAIL_EXCEPTION_SEND_MESSAGE_WITH_ATTACHMENT,
                    new Object[] {to, subject, filename, fileType, err.getCause()});
        }
    }

    @Override
    public void sendToSupport(@NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String subject,
                              @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String message)
            throws EmailException {
        send(infoEmail, subject, message);
    }

    @Override
    public void sendToSupportWithAttachment(@NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR) String subject,
                                            @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)String message,
                                            String filename, String fileType, String fileContent) throws EmailException {
        sendWithAttachment(infoEmail, subject, message, filename, fileType, fileContent);
    }
}
