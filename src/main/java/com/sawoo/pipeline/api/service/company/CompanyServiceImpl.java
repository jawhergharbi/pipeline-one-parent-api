package com.sawoo.pipeline.api.service.company;

import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.model.company.Company;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.repository.company.CompanyRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
@Validated
public class CompanyServiceImpl extends BaseServiceImpl<CompanyDTO, Company, CompanyRepository, CompanyMapper> implements CompanyService {

    @Autowired
    public CompanyServiceImpl(CompanyRepository repository, CompanyMapper mapper) {
        super(repository, mapper, DBConstants.COMPANY_DOCUMENT);
    }

    @Override
    public Optional<Company> entityExists(CompanyDTO entityToCreate) {
        log.debug(
                "Checking entity existence. [type: {}, name: {}]",
                DBConstants.PROSPECT_DOCUMENT,
                entityToCreate.getName());
        return getRepository().findByName(entityToCreate.getName());
    }

    @Override
    public Optional<CompanyDTO> findByName(String name) {
        log.debug("Retrieve company by name. Name: [{}]", name);
        return getRepository()
                .findByName(name)
                .map(getMapper().getMapperOut()::getDestination);
    }
}
