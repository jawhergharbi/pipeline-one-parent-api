package com.sawoo.pipeline.api.dto.user;

import lombok.*;

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
