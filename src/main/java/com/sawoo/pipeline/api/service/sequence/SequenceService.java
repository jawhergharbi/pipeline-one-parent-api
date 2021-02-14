package com.sawoo.pipeline.api.service.sequence;

import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.repository.sequence.SequenceRepository;
import com.sawoo.pipeline.api.service.base.BaseProxyService;
import com.sawoo.pipeline.api.service.base.BaseService;

public interface SequenceService extends BaseService<SequenceDTO>, BaseProxyService<SequenceRepository, SequenceMapper> {
}
