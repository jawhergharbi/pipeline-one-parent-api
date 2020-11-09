package com.sawoo.pipeline.api.model;

import com.googlecode.jmapper.annotations.JMap;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Document(collection = DataStoreConstants.COMPANY_DOCUMENT)
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class Company extends BaseEntity {

    @Id
    @JMap
    private String id;

    @JMap
    private String name;

    @JMap
    private String url;

    @JMap
    private Integer headcount;
}
