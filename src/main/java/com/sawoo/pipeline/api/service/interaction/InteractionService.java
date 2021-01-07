package com.sawoo.pipeline.api.service.interaction;

import com.sawoo.pipeline.api.dto.interaction.InteractionDTO;
import com.sawoo.pipeline.api.repository.interaction.InteractionRepository;
import com.sawoo.pipeline.api.service.base.BaseProxyService;
import com.sawoo.pipeline.api.service.base.BaseService;

import java.util.List;

public interface InteractionService extends BaseService<InteractionDTO>, BaseProxyService<InteractionRepository, InteractionMapper> {

    List<InteractionDTO> findBy(List<String> leadIds, List<Integer> status, List<Integer> types);

}
