package com.sawoo.pipeline.api.service.infra.audit;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.dto.audit.VersionDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.repository.jql.QueryBuilder;
import org.javers.shadow.Shadow;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final Javers javers;

    @Override
    public <D, M> List<VersionDTO<D>> getVersions(M currentVersion, String id, JMapper<D, M> mapper) {
        log.debug("Querying versions for component {} with id {}", currentVersion.getClass(), id);
        List<Shadow<M>> ds = getShadows(currentVersion.getClass(), id);
        AtomicInteger index = new AtomicInteger();
        List<VersionDTO<D>> versions = ds.stream().map(d -> {
            VersionDTO<D> version = new VersionDTO<>();
            version.setEntity(mapper.getDestination(d.get()));
            version.setVersion(index.getAndIncrement());
            version.setAuthor(d.getCommitMetadata().getAuthor());
            version.setCreated(d.getCommitMetadata().getCommitDate());
            if ( !javers.compare(currentVersion, d.get()).hasChanges()) {
                version.setCurrentVersion(true);
            }
            return version;
        }).collect(Collectors.toList());

        log.debug(
                "{} versions has/have been found for component {} with id {}",
                versions.size(),
                currentVersion.getClass(),
                id);

        return versions;
    }

    private <T> List<Shadow<T>> getShadows(Class<?> entity, String id) {
        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(id, entity);
        List<Shadow<T>> shadows = javers.findShadows(jqlQuery.build());
        Collections.reverse(shadows);
        return shadows;
    }
}
