package com.sawoo.pipeline.api.model.user;

import com.googlecode.jmapper.annotations.JMap;
import com.sawoo.pipeline.api.model.BaseEntity;
import com.sawoo.pipeline.api.model.DBConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
@Document(collection = DBConstants.USER_TOKEN_DOCUMENT)
@SuperBuilder
public class UserToken extends BaseEntity {

    @JMap
    @Id
    private String id;

    @JMap
    private String token;

    @JMap
    private LocalDateTime expirationDate;

    @JMap
    private UserTokenType type;

    private String userId;
}
