package com.sawoo.pipeline.api.service.account;

import com.sawoo.pipeline.api.common.exceptions.ResourceNotFoundException;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.dto.client.ClientBasicDTO;
import com.sawoo.pipeline.api.service.base.BaseService;

import java.util.List;

public interface AccountService extends BaseService<AccountDTO> {

    List<AccountDTO> findAllByUser(String userId) throws ResourceNotFoundException;

}
