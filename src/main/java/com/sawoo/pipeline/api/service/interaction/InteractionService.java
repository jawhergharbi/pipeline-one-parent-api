package com.sawoo.pipeline.api.service.interaction;

import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.repository.interaction.InteractionRepository;
import com.sawoo.pipeline.api.service.base.BaseProxyService;
import com.sawoo.pipeline.api.service.base.BaseService;

public interface InteractionService extends BaseService<InteractionDTO>, BaseProxyService<InteractionRepository, InteractionMapper> {

}
