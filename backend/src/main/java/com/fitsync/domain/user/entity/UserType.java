package com.fitsync.domain.user.entity;

public enum UserType {
    MEMBER, TRAINER, ADMIN;

    public String getRole() {
        return "ROLE_" + this.name();
    }
}
