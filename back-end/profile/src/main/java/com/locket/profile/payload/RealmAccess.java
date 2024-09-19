package com.locket.profile.payload;

import lombok.Data;

import java.util.ArrayList;

@Data
public class RealmAccess {
    private ArrayList<String> roles;
}
