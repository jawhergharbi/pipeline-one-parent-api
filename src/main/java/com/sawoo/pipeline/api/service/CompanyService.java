package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.company.CompanyDTO;

import java.util.List;
import java.util.Optional;

public interface CompanyService {

    CompanyDTO create(CompanyDTO company) throws CommonServiceException;

    CompanyDTO update(CompanyDTO company);

    List<CompanyDTO> findAll();

    CompanyDTO findById(String id) throws ResourceNotFoundException;

    Optional<CompanyDTO> findByName(String name);

    CompanyDTO delete(String id) throws ResourceNotFoundException;
}
