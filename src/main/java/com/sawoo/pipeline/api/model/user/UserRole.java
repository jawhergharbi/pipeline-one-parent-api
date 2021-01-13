package com.sawoo.pipeline.api.model.user;

import java.util.Set;

public enum UserRole {

    USER(0),
    ADMIN(1),
    AST(2),
    MNG(3),
    CLIENT(4);

    private int roleValue;

    UserRole(int roleValue) {
        this.roleValue = roleValue;
    }

    public int getRoleValue() {
        return roleValue;
    }

    public static UserRole getDefaultRole(Set<String> roles) {
        UserRole defaultRole = USER;
        if (roles != null) {
            if (roles.contains(ADMIN.name())) {
                return ADMIN;
            } else if (roles.contains(MNG.name())) {
                return MNG;
            } else if (roles.contains(AST.name())) {
                return AST;
            } else if (roles.contains(CLIENT.name())) {
                return CLIENT;
            }
        }
        return defaultRole;
    }
}