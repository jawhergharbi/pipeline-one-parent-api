package com.sawoo.pipeline.api.service.infra.email;

import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.EmailException;
import com.sawoo.pipeline.api.dto.email.EmailWithAttachmentDTO;
import com.sawoo.pipeline.api.dto.email.EmailDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface EmailService {

    void send(@NotNull (message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
              @Valid EmailDTO email) throws EmailException;

    void sendWithAttachment(@NotNull (message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
                            @Valid EmailWithAttachmentDTO email)
            throws EmailException;
}
