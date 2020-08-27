package com.sawoo.pipeline.api.service;

import com.googlecode.jmapper.api.enums.MappingType;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;
import com.sawoo.pipeline.api.model.Company;
import com.sawoo.pipeline.api.repository.CompanyRepository;
import com.sawoo.pipeline.api.service.common.CommonServiceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@RequiredArgsConstructor
@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository repository;
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
        Company entity = mapper.getCompanyDTOToDomainMapper().getDestination(companyDTO);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        entity.setCreated(now);
        entity.setUpdated(now);
        entity = repository.save(entity);

        log.debug("Company has been successfully created. Entity: [{}]", entity);

        return mapper.getCompanyDomainToDTOMapper().getDestination(entity);
    }

    @Override
    public Optional<CompanyDTO> update(Long id, CompanyDTO companyDTO) {
        log.debug("Updating company with id: [{}]", id);

        return repository
                .findById(id)
                .map((company) -> {
                    company = mapper.getCompanyDTOToDomainMapper()
                            .getDestination(
                                    company,
                                    companyDTO,
                                    MappingType.ALL_FIELDS,
                                    MappingType.ONLY_VALUED_FIELDS);
                    company.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
                    repository.save(company);

                    log.debug("Company entity with id [{}] has been successfully updated. Updated data: {}", id, companyDTO);
                    return Optional.of(mapper.getCompanyDomainToDTOMapper().getDestination(company));
                })
                .orElseGet(() -> {
                    log.info("Company entity with id [{}] does not exist", id);
                    return Optional.empty();
                });
    }

    @Override
    public List<CompanyDTO> findAll() {
        log.debug("Retrieving all company entities");
        List<CompanyDTO> companies = StreamSupport
                .stream(repository.findAll().spliterator(), false)
                .map((company) -> mapper.getCompanyDomainToDTOMapper().getDestination(company))
                .collect(Collectors.toList());
        log.debug("[{}] Company entity/entities has/have been found", companies.size());
        return companies;
    }

    @Override
    public CompanyDTO findById(Long id) throws ResourceNotFoundException {
        log.debug("Retrieving company by id. Id: [{}]", id);

        return repository
                .findById(id)
                .map((company) -> mapper.getCompanyDomainToDTOMapper().getDestination(company))
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                ExceptionMessageConstants.COMMON_GET_COMPONENT_RESOURCE_NOT_FOUND_EXCEPTION,
                                new String[]{"Company", String.valueOf(id)}));
    }

    @Override
    public Optional<CompanyDTO> findByName(String name) {
        log.debug("Retrieve company by name. Name: [{}]", name);

        return repository
                .findByName(name)
                .map(mapper.getCompanyDomainToDTOMapper()::getDestination);
    }

    @Override
    public Optional<CompanyDTO> delete(Long id) throws ResourceNotFoundException {
        log.debug("Deleting company entity with id: [{}]", id);

        return repository
                .findById(id)
                .map((company) -> {
                    repository.delete(company);
                    log.debug("Company entity with id: [{}] has been deleted", id);
                    return Optional.of(mapper.getCompanyDomainToDTOMapper().getDestination(company));
                })
                .orElseGet(() -> {
                    log.info("Company entity with id: [{}] does not exist", id);
                    return Optional.empty();
                });
    }
}
