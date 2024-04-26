package com.hit.joonggonara.type;

import lombok.Getter;

@Getter
public enum Role {

    USER, GUEST, ADMIN;

    public static Role checkRole(String role) {
        return switch (role){
            case "USER" ->  Role.USER;
            case "ADMIN" -> Role.ADMIN;
            default -> Role.GUEST;
        };
    }
}
