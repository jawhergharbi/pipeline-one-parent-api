package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;

import java.util.List;
import java.util.Optional;

public interface CompanyService {

    CompanyDTO create(CompanyDTO company) throws CommonServiceException;

    Optional<CompanyDTO> update(Long id, CompanyDTO company);

    List<CompanyDTO> findAll();

    CompanyDTO findById(Long id) throws ResourceNotFoundException;

    Optional<CompanyDTO> findByName(String name);

    Optional<CompanyDTO> delete(Long id) throws ResourceNotFoundException;
}
