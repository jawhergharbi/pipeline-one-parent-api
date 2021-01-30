package com.sawoo.pipeline.api.service.user;

import com.sawoo.pipeline.api.dto.user.UserTokenDTO;
import com.sawoo.pipeline.api.model.DBConstants;
import com.sawoo.pipeline.api.model.user.UserToken;
import com.sawoo.pipeline.api.model.user.UserTokenType;
import com.sawoo.pipeline.api.repository.user.UserTokenRepository;
import com.sawoo.pipeline.api.service.base.BaseServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
@Validated
public class UserTokenServiceImpl extends BaseServiceImpl<UserTokenDTO, UserToken, UserTokenRepository, UserTokenMapper> implements UserTokenService {

    @Autowired
    public UserTokenServiceImpl(UserTokenRepository repository, UserTokenMapper mapper) {
        super(repository, mapper, DBConstants.USER_TOKEN_DOCUMENT);
    }

    @Override
    public Optional<UserToken> entityExists(UserTokenDTO entityToCreate) {
        log.debug(
                "Checking entity existence. [type: {}, id: {}]",
                DBConstants.USER_TOKEN_DOCUMENT,
                entityToCreate.getId());
        return getRepository().findById(entityToCreate.getId());
    }

    @Override
    public Optional<UserTokenDTO> findByToken(String token) {
        log.debug("Retrieve user token by token. Token: [{}]", token);
        return getRepository()
                .findByToken(token)
                .map(getMapper().getMapperOut()::getDestination);
    }

    @Override
    public List<UserTokenDTO> findAllByUserId(String userId) {
        log.debug("Retrieving user token list by user. UserId: [{}]", userId);
        List<UserToken> tokenList = getRepository().findAllByUserId(userId);

        log.debug("[{}] token/s has/have been found for user id [{}]", tokenList.size(), userId);

        return mapUserTokenList(tokenList);
    }

    @Override
    public List<UserTokenDTO> findAllByUserIdAndType(String userId, UserTokenType type) {
        log.debug("Retrieving user token list by user and type. [userId: {}, type: {}]", userId, type);
        List<UserToken> tokenList = getRepository().findAllByUserIdAndType(userId, type);

        log.debug("[{}] token/s has/have been found for user id [{}]", tokenList.size(), userId);

        return mapUserTokenList(tokenList);
    }

    private List<UserTokenDTO> mapUserTokenList(List<UserToken> tokens) {
        return tokens
                .stream()
                .map( ut -> getMapper().getMapperOut().getDestination(ut))
                .collect(Collectors.toList());
    }
}
