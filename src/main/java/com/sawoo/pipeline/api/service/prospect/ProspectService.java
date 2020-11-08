package com.sawoo.pipeline.api.service.prospect;

import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.lead.ProspectDTO;
import com.sawoo.pipeline.api.service.BaseService;

public interface ProspectService extends BaseService<ProspectDTO> {

    ProspectDTO update(ProspectDTO prospect) throws ResourceNotFoundException;
}
