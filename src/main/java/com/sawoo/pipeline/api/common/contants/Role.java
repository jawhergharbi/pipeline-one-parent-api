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
        return defaultRole;
    }
}
