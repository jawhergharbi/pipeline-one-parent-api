package com.sawoo.pipeline.api.common.contants;

import java.util.Set;

public enum Role {

    USER(0),
    ADMIN(1),
    AST(2),
    MNG(3),
    CLIENT(4);

    private int roleValue;

    Role(int roleValue) {
        this.roleValue = roleValue;
    }

    public int getRoleValue() {
        return roleValue;
    }

    public static Role getDefaultRole(Set<String> roles) {
        Role defaultRole = USER;
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
