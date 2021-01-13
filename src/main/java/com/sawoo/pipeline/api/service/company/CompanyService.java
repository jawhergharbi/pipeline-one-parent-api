package com.sawoo.pipeline.api.service.company;

import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.service.base.BaseService;

import java.util.Optional;

public interface CompanyService extends BaseService<CompanyDTO> {

    Optional<CompanyDTO> findByName(String name);
}
