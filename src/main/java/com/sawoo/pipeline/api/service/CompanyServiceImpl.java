package com.sawoo.pipeline.api.service;

import com.googlecode.jmapper.api.enums.MappingType;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.model.CompanyMongoDB;
import com.sawoo.pipeline.api.repository.CompanyRepositoryMongo;
import com.sawoo.pipeline.api.service.common.CommonServiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Validated
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepositoryMongo repository;
    private final CommonServiceMapper mapper;

    @Override
    public CompanyDTO create(CompanyDTO companyDTO) throws CommonServiceException {
        log.debug("Creating new company. Name: [{}]", companyDTO.getName());

        repository
                .findByName(companyDTO.getName())
                .ifPresent((company) -> {
                    throw new CommonServiceException(
                            ExceptionMessageConstants.COMMON_CREATE_ENTITY_ALREADY_EXISTS_EXCEPTION,
                            new String[]{"Company", companyDTO.getName()});
                });
        CompanyMongoDB entity = mapper.getCompanyDTOToDomainMapper().getDestination(companyDTO);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        entity.setCreated(now);
        entity.setUpdated(now);
        entity = repository.insert(entity);

        log.debug("Company has been successfully created. Entity: [{}]", entity);

        return mapper.getCompanyDomainToDTOMapper().getDestination(entity);
    }

    @Override
    public CompanyDTO update(CompanyDTO companyToUpdate) throws ResourceNotFoundException {
        log.debug("Updating company with id: [{}]", companyToUpdate.getId());

        return repository
                .findById(companyToUpdate.getId())
                .map((company) -> {
                    company = mapper.getCompanyDTOToDomainMapper()
                            .getDestination(
                                    company,
                                    companyToUpdate,
                                    MappingType.ALL_FIELDS,
                                    MappingType.ONLY_VALUED_FIELDS);
                    company.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
                    repository.save(company);

                    log.debug(
                            "Company entity with id [{}] has been successfully updated. Updated data: [{}]",
                            companyToUpdate.getId(),
                            company);
                    return mapper.getCompanyDomainToDTOMapper().getDestination(company);
                })
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_UPDATE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{"Company", companyToUpdate.getId()}));
    }

    @Override
    public List<CompanyDTO> findAll() {
        log.debug("Retrieving all company entities");
        List<CompanyDTO> companies = repository
                .findAll()
                .stream()
                .map(mapper.getCompanyDomainToDTOMapper()::getDestination)
                .collect(Collectors.toList());
        log.debug("[{}] company entity/entities has/have been found", companies.size());
        return companies;
    }

    @Override
    public CompanyDTO findById(String id) throws ResourceNotFoundException {
        log.debug("Retrieving company by id. Id: [{}]", id);

        return repository
                .findById(id)
                .map((company) -> mapper.getCompanyDomainToDTOMapper().getDestination(company))
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{"Company", id}));
    }

    @Override
    public Optional<CompanyDTO> findByName(String name) {
        log.debug("Retrieve company by name. Name: [{}]", name);

        return repository
                .findByName(name)
                .map(mapper.getCompanyDomainToDTOMapper()::getDestination);
    }

    @Override
    public CompanyDTO delete(String id) throws ResourceNotFoundException {
        log.debug("Deleting company entity with id: [{}]", id);

        return repository
                .findById(id)
                .map((company) -> {
                    repository.delete(company);
                    log.debug("Company entity with id: [{}] has been deleted", id);
                    return mapper.getCompanyDomainToDTOMapper().getDestination(company);
                })
                .orElseThrow(() -> new ResourceNotFoundException(
                        ExceptionMessageConstants.COMMON_DELETE_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                        new String[]{"Company", id}));
    }
}
