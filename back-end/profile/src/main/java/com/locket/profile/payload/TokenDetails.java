package com.locket.profile.payload;

import lombok.Data;

import java.util.ArrayList;

@Data
public class TokenDetails {
    private int exp;
    private int iat;
    private String jti;
    private String iss;
    private String aud;
    private String sub;
    private String typ;
    private String azp;
    private String sid;
    private String acr;
    private ArrayList<String> allowedOrigins;
    private RealmAccess realm_access;
    private ResourceAccess resource_access;
    private String scope;
    private boolean email_verified;
    private String name;
    private String preferred_username;
    private String given_name;
    private String family_name;
    private String email;
    private String client_id;
    private String username;
    private String token_type;
    private boolean active;
}

