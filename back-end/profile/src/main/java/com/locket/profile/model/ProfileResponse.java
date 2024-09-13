package com.locket.profile.model;

import lombok.Data;

@Data
public class ProfileResponse {
    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean emailVerified;
    private Long createdTimestamp;
}
