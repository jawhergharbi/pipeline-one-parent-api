package com.sawoo.pipeline.api.service;

import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.prospect.LeadDTOOld;
import com.sawoo.pipeline.api.dto.prospect.LeadMainDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LeadServiceOld {

    LeadDTOOld create(LeadDTOOld lead, int type) throws CommonServiceException;

    LeadDTOOld findById(Long id) throws ResourceNotFoundException;

    List<LeadDTOOld> findAll();

    List<LeadMainDTO> findAllMain(LocalDateTime datetime);

    Optional<LeadDTOOld> delete(Long id);

    Optional<LeadDTOOld> update(Long id, LeadDTOOld lead);

    byte[] getReport(Long id, String type, String lan) throws CommonServiceException, ResourceNotFoundException;
}
