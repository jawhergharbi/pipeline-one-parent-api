package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.lead.LeadBasicDTO;
import com.sawoo.pipeline.api.dto.lead.LeadMainDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LeadService {

    LeadBasicDTO create(LeadBasicDTO lead) throws CommonServiceException;

    LeadBasicDTO findById(Long id) throws ResourceNotFoundException;

    List<LeadBasicDTO> findAll();

    List<LeadMainDTO> findAllMain(LocalDateTime datetime);

    Optional<LeadBasicDTO> delete(Long id);

    Optional<LeadBasicDTO> update(Long id, LeadBasicDTO lead);
}
