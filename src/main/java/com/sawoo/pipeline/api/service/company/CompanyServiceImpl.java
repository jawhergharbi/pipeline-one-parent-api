package com.sawoo.pipeline.api.service.company;

import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.model.CompanyMongoDB;
import com.sawoo.pipeline.api.model.DataStoreConstants;
import com.sawoo.pipeline.api.repository.CompanyRepository;
import com.sawoo.pipeline.api.service.BaseServiceImpl;
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
public class CompanyServiceImpl extends BaseServiceImpl<CompanyDTO, CompanyMongoDB, CompanyRepository> implements CompanyService {

    @Autowired
    public CompanyServiceImpl(CompanyRepository repository, CompanyMapper mapper) {
        super(repository, mapper, DataStoreConstants.COMPANY_DOCUMENT);
    }

    @Override
    public Optional<CompanyMongoDB> entityExists(CompanyDTO entityToCreate) {
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
