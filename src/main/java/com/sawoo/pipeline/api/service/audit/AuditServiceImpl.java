package com.sawoo.pipeline.api.service.audit;

import com.sawoo.pipeline.api.dto.audit.VersionDTO;
import lombok.RequiredArgsConstructor;
import org.javers.core.Javers;
import org.javers.repository.jql.QueryBuilder;
import org.javers.shadow.Shadow;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final Javers javers;

    @Override
    public <D> List<VersionDTO<D>> getVersions(D currentVersion, String id) {
        List<Shadow<D>> ds = getShadows(currentVersion.getClass(), id);
        AtomicInteger index = new AtomicInteger();
        return ds.stream().map(d -> {
            VersionDTO<D> version = new VersionDTO<>();
            version.setEntity(d.get());
            version.setVersion(index.getAndIncrement());
            version.setAuthor(d.getCommitMetadata().getAuthor());
            version.setCreated(d.getCommitMetadata().getCommitDate());
            if ( !javers.compare(currentVersion, d.get()).hasChanges()) {
                version.setCurrentVersion(true);
            }
            return version;
        }).collect(Collectors.toList());
    }

    private <T> List<Shadow<T>> getShadows(Class<?> entity, String id) {
        QueryBuilder jqlQuery = QueryBuilder.byInstanceId(id, entity);
        List<Shadow<T>> shadows = javers.findShadows(jqlQuery.build());
        Collections.reverse(shadows);
        return shadows;
    }
}
