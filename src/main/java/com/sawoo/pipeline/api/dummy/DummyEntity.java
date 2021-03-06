package com.sawoo.pipeline.api.dummy;

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
@Document(collection = "dummy")
public class DummyEntity {

    @Id
    private String id;
    private String name;
    private Integer number;
    private Integer version;
    private LocalDateTime dateTime;
}
