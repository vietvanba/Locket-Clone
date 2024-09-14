package com.locket.profile.service.impl;

import com.locket.profile.payload.ProfileResponse;
import com.locket.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.resource.UsersResource;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    private final ModelMapper modelMapper;
    private final UsersResource usersResource;

    @Override
    public List<ProfileResponse> getAllUser() {
        List<ProfileResponse> list = new ArrayList<>();
        log.info("Get all users");
        usersResource.list().stream().map(x ->
                modelMapper.map(x, ProfileResponse.class)
        ).forEach(list::add);
        return list;
    }
}
