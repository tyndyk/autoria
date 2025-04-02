package com.example.auto_ria.enums;

import com.example.auto_ria.exceptions.user.InvalidUserRoleException;

public enum ERole {
    ADMIN_ROOT(2),
    ADMIN(1),
    MANAGER(1),
    USER(1);

    private final int priority;

    ERole(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public static ERole getMostAbstractRole(ERole[] roles) {

        for (ERole role : roles) {
            if (role.getPriority() == 1) {
               return role;
            }
        }

        throw new InvalidUserRoleException("User has no first priority role.");
    }
}
