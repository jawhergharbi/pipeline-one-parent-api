package com.sawoo.pipeline.api.dummy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.cloud.gcp.data.datastore.core.mapping.Entity;
import org.springframework.data.annotation.Id;

@Entity(name = "dummy")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class DummyEntity {

    @Id
    private String id;
    private String name;
    private Integer number;
    private Integer version;
}
