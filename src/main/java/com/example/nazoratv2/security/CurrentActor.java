package com.example.nazoratv2.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CurrentActor {

    private Long id;
    private String role;
    private String name;
    private boolean anonymous;

    public static CurrentActor user(Long id, String role, String name) {
        return new CurrentActor(id, role, name, false);
    }

    public static CurrentActor student(Long id, String role, String name) {
        return new CurrentActor(id, role, name, false);
    }

    public static CurrentActor anonymous() {
        return new CurrentActor(null, "ANONYMOUS", "Anonymous", true);
    }
}
