package com.sawoo.pipeline.api.service.sequencestep;

import com.sawoo.pipeline.api.dto.sequence.SequenceStepDTO;
import com.sawoo.pipeline.api.repository.sequencestep.SequenceStepRepository;
import com.sawoo.pipeline.api.service.base.BaseProxyService;
import com.sawoo.pipeline.api.service.base.BaseService;

public interface SequenceStepService extends BaseService<SequenceStepDTO>, BaseProxyService<SequenceStepRepository, SequenceStepMapper> {

}
