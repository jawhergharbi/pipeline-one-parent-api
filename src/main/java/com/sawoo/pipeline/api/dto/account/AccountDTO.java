package com.sawoo.pipeline.api.dto.account;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.model.common.Status;
import com.sawoo.pipeline.api.model.common.Note;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AccountDTO {

    @JMap
    private String id;

    @JMap
    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private String fullName;

    @JMap
    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private String position;

    @JMap
    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private String linkedInUrl;

    @JMap
    @Valid
    @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
    private CompanyDTO company;

    @JMap
    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private String email;

    @JMap
    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private String phoneNumber;

    @JMap
    private Note notes;

    @JMap
    private Status status;

    @JMap
    private Set<UserAuthDTO> users;

    @JMap
    private LocalDateTime created;

    @JMap
    private LocalDateTime updated;

    public Set<UserAuthDTO> getUsers() {
        if (users == null) {
            users = new HashSet<>();
        }
        return users;
    }
}
