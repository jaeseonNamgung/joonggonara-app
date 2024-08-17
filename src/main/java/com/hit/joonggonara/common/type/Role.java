package com.hit.joonggonara.common.type;

import lombok.Getter;

@Getter
public enum Role {

    ROLE_USER, ROLE_GUEST, ROLE_ADMIN;

    public static Role checkRole(String role) {
        role = role.toUpperCase();
        return switch (role){
            case "ROLE_USER" ->  Role.ROLE_USER;
            case "ROLE_ADMIN" -> Role.ROLE_ADMIN;
            default -> Role.ROLE_GUEST;
        };
    }
}
