package com.sawoo.pipeline.api.service.infra.email;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.EmailException;
import com.sawoo.pipeline.api.dto.email.EmailDTO;
import com.sawoo.pipeline.api.dto.email.EmailWithAttachmentDTO;
import com.sawoo.pipeline.api.dto.email.EmailWithTemplateDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.thymeleaf.context.Context;
import org.thymeleaf.exceptions.TemplateInputException;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    public void send(@NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
                     @Valid EmailDTO email)
            throws EmailException {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(email.getTo());
            mailMessage.setSubject(email.getSubject());
            mailMessage.setText(email.getMessage());

            mailSender.send(mailMessage);

            log.debug("Email correctly sent to: {}", email.getTo());
        } catch (MailException err) {
            throw new EmailException(
                    ExceptionMessageConstants.MAIL_EXCEPTION_SEND_MESSAGE,
                    new Object[]{email.getTo(), email.getSubject(), err.getCause()});
        }
    }

    @Override
    public void sendWithAttachment(
            @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
            @Valid EmailWithAttachmentDTO email) throws EmailException {

        try {
            log.debug("Sending email with attachment to: {} subject: {} attachment: {}",
                    email.getTo(),
                    email.getSubject(),
                    email.getFileName());

            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, CharEncoding.UTF_8);
            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            helper.setText(email.getMessage(), false);
            String FILE_TYPE_CONTENT = "text/plain; charset=UTF-8";
            helper.addAttachment(email.getFileName(),
                    new ByteArrayDataSource(email.getFileContent(),
                            email.getFileType() == null ? FILE_TYPE_CONTENT : email.getFileType())
            );
            mailSender.send(msg);

            log.debug("Email correctly sent to: {}", email.getTo());
        } catch (MessagingException err) {
            throw new EmailException(
                    ExceptionMessageConstants.MAIL_EXCEPTION_SEND_MESSAGE_WITH_ATTACHMENT,
                    new Object[]{email.getTo(), email.getSubject(), email.getFileName(), email.getFileType(), err.getCause()});
        }
    }

    @Override
    public void sendWithTemplate(
            @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
            @Valid EmailWithTemplateDTO email) throws EmailException {
        try {
            log.debug("Sending email with template to: {} subject: {} attachment: {}",
                    email.getTo(),
                    email.getSubject(),
                    email.getTemplateName());
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            String html = getHtmlContent(email);

            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            helper.setText(html, true);

            mailSender.send(message);

            log.debug("Email correctly sent to: {}", email.getTo());
        } catch (MessagingException | TemplateInputException err) {
            throw new EmailException(
                    ExceptionMessageConstants.MAIL_EXCEPTION_SEND_MESSAGE_WITH_TEMPLATE,
                    new Object[]{email.getTo(), email.getSubject(), email.getTemplateName(), err.getCause()});
        }
    }

    private String getHtmlContent(EmailWithTemplateDTO email) {
        Context context = new Context();
        context.setVariables(email.getTemplateContext());
        return templateEngine.process(email.getTemplateName(), context);
    }
}
