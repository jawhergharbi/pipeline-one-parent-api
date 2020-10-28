package com.sawoo.pipeline.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
