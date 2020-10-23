package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.lead.LeadDTO;
import com.sawoo.pipeline.api.dto.lead.LeadMainDTO;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LeadService {

    LeadDTO create(LeadDTO lead) throws CommonServiceException;

    LeadDTO findById(Long id) throws ResourceNotFoundException;

    List<LeadDTO> findAll();

    List<LeadMainDTO> findAllMain(LocalDateTime datetime);

    Optional<LeadDTO> delete(Long id);

    Optional<LeadDTO> update(Long id, LeadDTO lead);

    byte[] getReport(Long id, String type, String lan) throws CommonServiceException, ResourceNotFoundException;
}
