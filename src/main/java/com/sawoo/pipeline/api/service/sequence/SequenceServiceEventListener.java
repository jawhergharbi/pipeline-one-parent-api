package com.sawoo.pipeline.api.service.sequence;

import com.sawoo.pipeline.api.dto.sequence.SequenceDTO;
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
        Set<SequenceUser> users = dto.getUsers();
        Consumer<SequenceUser> setTimeStamps = u -> {
            u.setCreated(LocalDateTime.now(ZoneOffset.UTC));
            u.setUpdated(u.getCreated());
        };
        users.forEach(setTimeStamps);
    }

    @Override
    public void onBeforeSave(SequenceDTO dto, Sequence entity) {
        Set<SequenceUser> users = dto.getUsers();
        Consumer<SequenceUser> setTimeStamps = u -> u.setUpdated(LocalDateTime.now(ZoneOffset.UTC));
        users.forEach(setTimeStamps);
    }

    @Override
    public void onBeforeUpdate(SequenceDTO dto, Sequence entity) {
        if (dto.getUsers().size() > 0) {
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
