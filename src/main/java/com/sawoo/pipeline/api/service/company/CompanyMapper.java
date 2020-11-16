package com.sawoo.pipeline.api.service.company;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.model.company.Company;
import com.sawoo.pipeline.api.service.base.BaseMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@Component
public class CompanyMapper implements BaseMapper<CompanyDTO, Company> {

    private final JMapper<CompanyDTO, Company> mapperOut = new JMapper<>(CompanyDTO.class, Company.class);
    private final JMapper<Company, CompanyDTO> mapperIn = new JMapper<>(Company.class, CompanyDTO.class);
}
