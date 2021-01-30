package com.sawoo.pipeline.api.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.common.contants.CommonConstants;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.dto.BaseEntityDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Set;

/****
 * Apparently JsonIgnore does not work at property level with lombok
 * https://github.com/FasterXML/jackson-databind/issues/1226
 */


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@JsonIgnoreProperties(value = {"password", "confirmPassword"}, allowSetters = true)
public class UserAuthDTO extends BaseEntityDTO {

    @JMap
    @Id
    private String id;

    @JMap
    @Size(max = 100, message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_EXCEED_MAX_SIZE_ERROR)
    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private String fullName;

    @Size(min = CommonConstants.AUTH_PASSWORD_MIN_LENGTH,
            message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_BELLOW_MIN_SIZE_ERROR)
    @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
    @ToString.Exclude
    private String password;

    @Size(min = CommonConstants.AUTH_PASSWORD_MIN_LENGTH,
            message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_BELLOW_MIN_SIZE_ERROR)
    @NotNull(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_NULL_ERROR)
    @ToString.Exclude
    private String confirmPassword;

    @JMap
    @NotBlank(message = ExceptionMessageConstants.COMMON_FIELD_CAN_NOT_BE_EMPTY_ERROR)
    private String email;

    @JMap
    private Boolean active;

    @JMap
    private Set<String> roles;

    @JMap
    private LocalDateTime lastLogin;
}
