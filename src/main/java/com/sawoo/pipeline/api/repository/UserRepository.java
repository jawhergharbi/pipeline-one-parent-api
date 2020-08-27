package com.sawoo.pipeline.api.repository;

import com.sawoo.pipeline.api.model.User;
import org.springframework.cloud.gcp.data.datastore.repository.DatastoreRepository;

import java.util.List;

public interface UserRepository extends DatastoreRepository<User, String> {
    List<User> findAllByRoles(String role);
}
