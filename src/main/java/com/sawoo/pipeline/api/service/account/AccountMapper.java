package com.sawoo.pipeline.api.service.account;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.dto.account.AccountDTO;
import com.sawoo.pipeline.api.dto.user.UserAuthDTO;
import com.sawoo.pipeline.api.model.account.Account;
import com.sawoo.pipeline.api.model.user.User;
import com.sawoo.pipeline.api.service.base.BaseMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Getter
@RequiredArgsConstructor
@Component
public class AccountMapper implements BaseMapper<AccountDTO, Account> {

    private final JMapper<AccountDTO, Account> mapperOut = new JMapper<>(AccountDTO.class, Account.class);
    private final JMapper<Account, AccountDTO> mapperIn = new JMapper<>(Account.class, AccountDTO.class);

    private final JMapper<UserAuthDTO, User> userMapperOut = new JMapper<>(UserAuthDTO.class, User.class);
    private final JMapper<User, UserAuthDTO> userMapperIn = new JMapper<>(User.class, UserAuthDTO.class);
}
