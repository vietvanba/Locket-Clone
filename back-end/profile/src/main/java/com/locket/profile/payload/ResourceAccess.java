package com.locket.profile.payload;

import lombok.Data;

import java.util.List;

@Data
public class ResourceAccess {
    private List<RealmRoles> realmRoles;
}
