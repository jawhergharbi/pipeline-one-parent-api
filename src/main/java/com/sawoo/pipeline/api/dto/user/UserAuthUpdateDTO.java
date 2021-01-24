package com.sawoo.pipeline.api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserAuthUpdateDTO extends UserAuthDTO {

    @ToString.Exclude
    private String password;

    @ToString.Exclude
    private String confirmPassword;
}
