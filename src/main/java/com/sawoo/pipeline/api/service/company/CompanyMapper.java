package com.sawoo.pipeline.api.service.company;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.model.CompanyMongoDB;
import com.sawoo.pipeline.api.service.BaseMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@Component
public class CompanyMapper implements BaseMapper<CompanyDTO, CompanyMongoDB> {

    private final JMapper<CompanyDTO, CompanyMongoDB> mapperOut = new JMapper<>(CompanyDTO.class, CompanyMongoDB.class);
    private final JMapper<CompanyMongoDB, CompanyDTO> mapperIn = new JMapper<>(CompanyMongoDB.class, CompanyDTO.class);
}
