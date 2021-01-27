package com.sawoo.pipeline.api.service.infra.email;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.EmailException;
import com.sawoo.pipeline.api.dto.email.EmailWithAttachmentDTO;
import com.sawoo.pipeline.api.dto.email.EmailDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Slf4j
@Service
@RequiredArgsConstructor
@Validated
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void send(@NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
                         @Valid EmailDTO email)
            throws EmailException {
        try {
            if (StringUtils.isEmpty(email.getTo())) {
                throw new EmailException(
                        ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_OR_NULL_ERROR,
                        new String[] {"to", getClass().getSimpleName()});
            }
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(email.getTo());
            mailMessage.setSubject(email.getSubject());
            mailMessage.setText(email.getMessage());

            log.debug("Sending email to: {} subject: {}", email.getTo(), email.getSubject());
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
            @Valid EmailWithAttachmentDTO email) throws EmailException {
        MimeMessage msg = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, CharEncoding.UTF_8);
            helper.setTo(email.getTo());
            helper.setSubject(email.getSubject());
            helper.setText(email.getMessage(), false);
            helper.addAttachment(email.getFileName(),
                    new ByteArrayDataSource(email.getFileContent(), email.getFileType())
            );
            mailSender.send(msg);
        } catch (MessagingException err) {
            throw new EmailException(
                    ExceptionMessageConstants.MAIL_EXCEPTION_SEND_MESSAGE_WITH_ATTACHMENT,
                    new Object[] {email.getTo(), email.getSubject(), email.getFileName(), email.getFileType(), err.getCause()});
        }
    }
}
