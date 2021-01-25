package com.sawoo.pipeline.api.service.infra.email;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.EmailException;
import com.sawoo.pipeline.api.dto.EmailDTO;
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
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.info-account}")
    private String infoEmail;

    @Override
    public void send(@NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
                         @Valid EmailDTO email)
            throws EmailException {
        Objects.requireNonNull(email.getTo());
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email.getTo());
        mailMessage.setSubject(email.getSubject());
        mailMessage.setText(email.getMessage());

        log.debug("Sending email to: {} subject: {}", email.getTo(), email.getSubject());

        try {
            mailSender.send(mailMessage);
        } catch (MailException err) {
            throw new EmailException(
                    ExceptionMessageConstants.MAIL_EXCEPTION_SEND_MESSAGE,
                    new Object[] {email.getTo(), email.getSubject(), err.getCause()});
        }
    }

    @Override
    public void sendWithAttachment(
            @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
            @Valid EmailDTO email,
            String filename, String fileType, String fileContent) throws EmailException {
        MimeMessage msg = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, CharEncoding.UTF_8);
            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            helper.setText(email.getMessage(), false);
            helper.addAttachment(filename,
                    new ByteArrayDataSource(fileContent.getBytes(), fileType)
            );
            mailSender.send(msg);
        } catch (MessagingException err) {
            throw new EmailException(
                    ExceptionMessageConstants.MAIL_EXCEPTION_SEND_MESSAGE_WITH_ATTACHMENT,
                    new Object[] {email.getTo(), email.getSubject(), filename, fileType, err.getCause()});
        }
    }

    @Override
    public void sendToSupport(
            @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
            @Valid EmailDTO email)
            throws EmailException {
        email.setTo(infoEmail);
        send(email);
    }

    @Override
    public void sendToSupportWithAttachment(@NotNull (message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
                                                @Valid EmailDTO email,
                                            String filename, String fileType, String fileContent) throws EmailException {
        email.setTo(infoEmail);
        sendWithAttachment(email, filename, fileType, fileContent);
    }
}
