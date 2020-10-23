package com.sawoo.pipeline.api.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDTO<T> {

    String template;
    T templateData;
    Integer type;
    String locale;
}
