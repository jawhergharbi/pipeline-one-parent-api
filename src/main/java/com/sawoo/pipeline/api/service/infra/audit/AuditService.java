package com.sawoo.pipeline.api.service.infra.audit;

import com.googlecode.jmapper.JMapper;
import com.sawoo.pipeline.api.dto.audit.VersionDTO;

import java.util.List;

public interface AuditService {

    public <D, M> List<VersionDTO<D>> getVersions(M currentVersion, String id, JMapper<D, M> mapper);
}
