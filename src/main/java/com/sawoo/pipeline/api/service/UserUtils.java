package com.sawoo.pipeline.api.service;


import com.sawoo.pipeline.api.common.contants.Role;

import java.util.Optional;
import java.util.Set;

public class UserUtils {

    public static Optional<Role> getOpsRole(Set<String> roles) {
        return roles
                .stream()
                .filter((role) -> role.equals(Role.MNG.name()) || role.equals(Role.AST.name()))
                .findFirst()
                .map(Role::valueOf);
    }
}
