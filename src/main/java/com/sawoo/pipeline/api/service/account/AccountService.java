package com.sawoo.pipeline.api.service.account;

import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.repository.account.AccountRepository;
import com.sawoo.pipeline.api.service.base.BaseProxyService;
import com.sawoo.pipeline.api.service.base.BaseService;

public interface AccountService extends BaseService<AccountDTO>, BaseProxyService<AccountRepository, AccountMapper>, AccountUserService, AccountLeadService {

}
