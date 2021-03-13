package com.sawoo.pipeline.api.service.sequence;

import com.google.api.client.util.Strings;
import com.sawoo.pipeline.api.common.CommonUtils;
import com.sawoo.pipeline.api.common.contants.ExceptionMessageConstants;
import com.sawoo.pipeline.api.common.exceptions.CommonServiceException;
import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
import com.sawoo.pipeline.api.dto.sequence.SequenceUserDTO;
import com.sawoo.pipeline.api.model.sequence.Sequence;
import com.sawoo.pipeline.api.model.sequence.SequenceStatus;
import com.sawoo.pipeline.api.model.sequence.SequenceUser;
import com.sawoo.pipeline.api.model.sequence.SequenceUserType;
import com.sawoo.pipeline.api.service.base.event.BaseServiceBeforeInsertEvent;
import com.sawoo.pipeline.api.service.base.event.BaseServiceBeforeSaveEvent;
import com.sawoo.pipeline.api.service.base.event.BaseServiceBeforeUpdateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

@Slf4j
@Component
public class SequenceServiceEventListener {

    @EventListener
    public void handleBeforeInsertEvent(BaseServiceBeforeInsertEvent<SequenceDTO, Sequence> event) {
        log.debug("Sequence before insert listener");
        Sequence entity = event.getModel();
        SequenceDTO dto = event.getDto();
        // status
        if (entity.getStatus() == null) {
            entity.setStatus(SequenceStatus.IN_PROGRESS);
        }
        // users
        Set<SequenceUser> users = entity.getUsers();
        if (CommonUtils.isNotEmptyNorNull(users)) {
            if (users.stream().noneMatch(u -> SequenceUserType.OWNER.equals(u.getType()))) {
                throw new CommonServiceException(
                        ExceptionMessageConstants.SEQUENCE_CREATE_USER_OWNER_NOT_SPECIFIED_EXCEPTION,
                        new String[] {entity.getName(), dto.getUsers().toString()});
            }
            Consumer<SequenceUser> setTimeStamps = u -> {
                u.setCreated(LocalDateTime.now(ZoneOffset.UTC));
                u.setUpdated(u.getCreated());
            };
            users.forEach(setTimeStamps);
        }
    }

    @EventListener
    public void handleBeforeSaveEvent(BaseServiceBeforeSaveEvent<SequenceDTO, Sequence> event) {
        log.debug("Sequence before save listener");
        SequenceDTO dto = event.getDto();
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

    @EventListener
    public void handleBeforeUpdateEvent(BaseServiceBeforeUpdateEvent<SequenceDTO, Sequence> event) {
        log.debug("Sequence before update listener");
        Sequence entity = event.getModel();
        SequenceDTO dto = event.getDto();
        Set<SequenceUserDTO> users = dto.getUsers();
        if (CommonUtils.isNotEmptyNorNull(users)) {
            validateUsers(users, entity.getId(), entity.getUsers());
            users.forEach(u -> updateSequenceUserList(u, entity.getUsers()));
            dto.getUsers().clear();
            log.debug("Users [{}] ready to be updated for sequence: [id: {}, name: {}]",
                    entity.getUsers(),
                    entity.getId(),
                    entity.getName());
        }
    }

    private void validateUsers(Set<SequenceUserDTO> users, String sequenceId, Set<SequenceUser> storedUsers) {
        users.forEach(u -> {
            // userId not informed throws exception
            if (Strings.isNullOrEmpty(u.getUserId())) {
                throw new CommonServiceException(
                        ExceptionMessageConstants.SEQUENCE_UPDATE_USER_ID_NOT_INFORMED_EXCEPTION,
                        new String[] {sequenceId});
            }
            // type not informed will set it to VIEWER
            if (u.getType() == null && !u.isToBeDeleted()) {
                u.setType(SequenceUserType.VIEWER);
            }

            // validation if user is going to be deleted
            validateUserIfToBeDeleted(u, sequenceId, storedUsers);
        });
    }

    private void updateSequenceUserList(SequenceUserDTO user, Set<SequenceUser> storedUsers) {
        if (user.isToBeDeleted()) {
            storedUsers.removeIf(u -> u.getUserId().equals(user.getUserId()));
        } else {
            storedUsers.stream().filter(u -> u.getUserId().equals(user.getUserId()))
                    .findAny()
                    .ifPresentOrElse((storedUser) -> updateUser(user, storedUser), () -> addUser(user, storedUsers));
        }
    }

    private void updateUser(SequenceUserDTO user, SequenceUser storedUsers) {
        storedUsers.setType(user.getType());
        storedUsers.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
    }

    private void addUser(SequenceUserDTO user, Set<SequenceUser> storedUsers) {
        if (user.getType().equals(SequenceUserType.OWNER)) {
            Optional<SequenceUser> owner = storedUsers
                    .stream()
                    .filter(storedUser -> storedUser.getType() == SequenceUserType.OWNER)
                    .findFirst();
            owner.ifPresent(o -> o.setType(SequenceUserType.EDITOR));
        }
        storedUsers.add(SequenceUser
                .builder()
                .userId(user.getUserId())
                .type(user.getType())
                .created(LocalDateTime.now(ZoneOffset.UTC))
                .updated(LocalDateTime.now(ZoneOffset.UTC))
                .build());
    }

    private void validateUserIfToBeDeleted(SequenceUserDTO user, String sequenceId, Set<SequenceUser> storedUsers) {
        if (user.isToBeDeleted()) {
            // not users in the sequence
            if (CommonUtils.isEmptyOrNull(storedUsers)) {
                throw new CommonServiceException(
                        ExceptionMessageConstants.SEQUENCE_UPDATE_DELETE_USER_LIST_EMPTY_EXCEPTION,
                        new String[]{sequenceId});
            }
            // user id not found
            if (storedUsers.stream().noneMatch(su -> user.getUserId().equals(su.getUserId()))) {
                throw new CommonServiceException(
                        ExceptionMessageConstants.SEQUENCE_UPDATE_DELETE_USER_NOT_FOUND_EXCEPTION,
                        new String[]{sequenceId, user.getUserId()});
            }
            // user id is owner
            if (storedUsers.stream().anyMatch(
                    su -> user.getUserId().equals(su.getUserId()) &&
                            SequenceUserType.OWNER.equals(su.getType()))) {
                throw new CommonServiceException(
                        ExceptionMessageConstants.SEQUENCE_UPDATE_DELETE_USER_OWNER_EXCEPTION,
                        new String[]{sequenceId, user.toString()});
            }
        }
    }
}
