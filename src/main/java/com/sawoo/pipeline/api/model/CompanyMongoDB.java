package com.sawoo.pipeline.api.model;

import com.googlecode.jmapper.annotations.JMap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = DataStoreConstants.COMPANY_DOCUMENT)
public class CompanyMongoDB {

    @Id
    @JMap
    private String id;

    @JMap
    private String name;

    @JMap
    private String url;

    @JMap
    private Integer headcount;

    @JMap
    private LocalDateTime created;

    @JMap
    private LocalDateTime updated;
}
