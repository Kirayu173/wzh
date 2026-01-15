package com.wzh.suyuan.event;

public class AuthEvent {
    public enum Type {
        UNAUTHORIZED
    }

    private final Type type;

    public AuthEvent(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }
}
