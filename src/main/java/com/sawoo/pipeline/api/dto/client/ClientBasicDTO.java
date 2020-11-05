package com.sawoo.pipeline.api.dto.client;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.dto.NoteDTO;
import com.sawoo.pipeline.api.dto.StatusDTO;
import com.sawoo.pipeline.api.dto.user.UserDTO;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ClientBasicDTO extends ClientBaseDTO {

    @JMap
    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private String email;

    @JMap
    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private String phoneNumber;

    @JMap
    private NoteDTO notes;

    @JMap
    private StatusDTO status;

    @JMap("leads")
    private int leadsSize;

    @JMap
    private UserDTO salesAssistant;

    @JMap
    private UserDTO customerSuccessManager;

    @JMap
    private LocalDateTime created;

    @JMap
    private LocalDateTime updated;
}
