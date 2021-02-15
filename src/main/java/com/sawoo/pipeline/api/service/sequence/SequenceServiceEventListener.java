package com.sawoo.pipeline.api.service.sequence;

import com.google.api.client.util.Strings;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.dto.sequence.SequenceUserDTO;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.model.sequence.SequenceUser;
import com.sawoo.pipeline.api.model.sequence.SequenceUserType;
import com.sawoo.pipeline.api.service.base.BaseServiceEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Slf4j
@Component
public class SequenceServiceEventListener implements BaseServiceEventListener<SequenceDTO, Sequence> {

    @Override
    public void onBeforeInsert(SequenceDTO dto, Sequence entity) {
        Set<SequenceUserDTO> users = dto.getUsers();
        if (users != null && users.size() > 0) {
            if (entity.getUsers().size() > 0 &&
                    dto.getUsers().stream().noneMatch(u -> SequenceUserType.OWNER.equals(u.getType()))) {
                throw new CommonServiceException(
                        ExceptionMessageConstants.SEQUENCE_CREATE_USER_OWNER_NOT_SPECIFIED_EXCEPTION,
                        new String[] {entity.getName(), dto.getUsers().toString()});
            }
            Consumer<SequenceUserDTO> setTimeStamps = u -> {
                u.setCreated(LocalDateTime.now(ZoneOffset.UTC));
                u.setUpdated(u.getCreated());
            };
            users.forEach(setTimeStamps);
        }
    }

    @Override
    public void onBeforeSave(SequenceDTO dto, Sequence entity) {
        Set<SequenceUserDTO> users = dto.getUsers();
        if (users != null && users.size() > 0) {
            Consumer<SequenceUserDTO> setTimeStamps = u -> {
                u.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
                if (u.getCreated() == null) {
                    u.setCreated(u.getUpdated());
                }
            };
            users.forEach(setTimeStamps);
        }
    }

    @Override
    public void onBeforeUpdate(SequenceDTO dto, Sequence entity) {
        if (dto.getUsers() != null && dto.getUsers().size() > 0) {
            Set<SequenceUser> storedUsers = entity.getUsers();
            dto.getUsers().forEach((u) -> storedUsers.stream().filter(user -> user.getUserId().equals(u.getUserId()))
                    .findFirst()
                    .ifPresentOrElse(storedUser -> {
                        log.info("User id: [{}] was already part of the sequence's [id: {}, name: {}] users",
                                u.getUserId(),
                                entity.getId(),
                                entity.getName());
                        storedUser.setType(u.getType());
                    }, () -> {
                        if (Strings.isNullOrEmpty(u.getUserId())) {
                            throw new CommonServiceException(
                                    ExceptionMessageConstants.SEQUENCE_UPDATE_USER_ID_NOT_INFORMED_EXCEPTION,
                                    new String[] {entity.getId()});
                        }
                        if (u.getType() == SequenceUserType.OWNER) {
                           Optional<SequenceUser> owner = storedUsers
                                   .stream()
                                   .filter(storedUser -> storedUser.getType() == SequenceUserType.OWNER)
                                   .findFirst();
                           owner.ifPresent(o -> o.setType(SequenceUserType.EDITOR));
                        }
                        storedUsers.add(SequenceUser
                                .builder()
                                .userId(u.getUserId())
                                .type(u.getType())
                                .created(LocalDateTime.now(ZoneOffset.UTC))
                                .updated(LocalDateTime.now(ZoneOffset.UTC))
                                .build());
                    }));
            log.debug("Users [{}] ready to be updated for sequence: [id: {}, name: {}]",
                    storedUsers,
                    entity.getId(),
                    entity.getName());
        }
    }
}
