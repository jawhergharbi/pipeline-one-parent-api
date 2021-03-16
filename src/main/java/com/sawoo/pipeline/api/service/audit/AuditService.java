package com.sawoo.pipeline.api.service.audit;

import com.sawoo.pipeline.api.dto.audit.VersionDTO;

import java.util.List;

public interface AuditService {

    <D> List<VersionDTO<D>> getVersions(D currentVersion, String id);
}
