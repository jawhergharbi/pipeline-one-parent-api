package com.sawoo.pipeline.api.repository;

import com.sawoo.pipeline.api.model.UserOld;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;

import java.util.List;

public interface UserRepository extends DatastoreRepository<UserOld, String> {
    List<UserOld> findAllByRoles(String role);
}
