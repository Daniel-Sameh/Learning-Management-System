package com.swe.lms.userManagement.Service;

import com.swe.lms.security.dao.request.SignUpRequest;
import com.swe.lms.security.dao.request.SigninRequest;
import com.swe.lms.security.dao.response.JwtAuthenticationResponse;
import com.swe.lms.userManagement.entity.User;

import java.util.List;
import java.util.Map;

public interface AuthenticationService {
    JwtAuthenticationResponse signup(SignUpRequest request);

    JwtAuthenticationResponse signin(SigninRequest request);
    String changeRole(long userId, String role);

    String deleteUser(long userId);
    List<User> getUsers();
    User getUserByUsername(String username);

     User updateprofile(Map<String, Object>payload, String token,Long userid);
}
