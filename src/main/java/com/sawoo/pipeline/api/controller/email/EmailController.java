package com.sawoo.pipeline.api.controller.email;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.common.exceptions.EmailException;
import com.sawoo.pipeline.api.controller.ControllerConstants;
import com.sawoo.pipeline.api.dto.email.EmailDTO;
import com.sawoo.pipeline.api.dto.email.EmailWithAttachmentDTO;
import com.sawoo.pipeline.api.dto.email.EmailWithAttachmentRequestDTO;
import com.sawoo.pipeline.api.dto.email.EmailWithTemplateDTO;
import com.sawoo.pipeline.api.service.infra.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(ControllerConstants.EMAIL_CONTROLLER_API_BASE_URI)
public class EmailController {

    private final EmailService emailService;
    private final JMapper<EmailWithAttachmentDTO, EmailWithAttachmentRequestDTO> mapper = new JMapper<>(EmailWithAttachmentDTO.class, EmailWithAttachmentRequestDTO.class);

    @RequestMapping(
            method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> send(@Valid @RequestBody EmailDTO email) throws EmailException {
        emailService.send(email);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            value = "/attachment",
            method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> sendWithAttachment(@Valid @RequestBody EmailWithAttachmentRequestDTO emailRequest)
            throws EmailException {
        EmailWithAttachmentDTO email = mapper.getDestination(emailRequest);
        /**
         * We don't add any file content in the controller, just for the sake of testing this component we set the fileContent
         * using the filePath. In a normal use-case it would be the client of the email service the responsible for reading the
         * file and setting the content
         */
        email.setFileContent(emailRequest.getFilePath().getBytes(StandardCharsets.UTF_8));
        emailService.sendWithAttachment(email);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(
            value = "/template",
            method = RequestMethod.POST,
            consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Void> sendWithTemplate(@Valid @RequestBody EmailWithTemplateDTO email)
            throws EmailException {
        emailService.sendWithTemplate(email);
        return ResponseEntity.ok().build();
    }
}
