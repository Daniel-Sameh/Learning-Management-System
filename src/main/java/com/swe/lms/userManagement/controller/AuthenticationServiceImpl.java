package com.swe.lms.userManagement.controller;

import com.swe.lms.security.JwtService;
import com.swe.lms.security.dao.request.SignUpRequest;
import com.swe.lms.security.dao.request.SigninRequest;
import com.swe.lms.security.dao.response.JwtAuthenticationResponse;
import com.swe.lms.userManagement.entity.User;
import com.swe.lms.userManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.swe.lms.userManagement.entity.Role;
@Service
@RequiredArgsConstructor

public class AuthenticationServiceImpl implements AuthenticationService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    public AuthenticationResponse register(RegisterRequest request) {
//        var user= User.builder()
//        .username((request.getUsername()))
//        .email(request.getEmail())
//        .password(passwordEncoder.encode(request.getPassword()))
//        .role(Role.USER)
//        .build()
//        .userRepository.save(user);
//        var jwtToken=jwtService.generateToken(user);
//        return AuthenticationResponse.builder()
//                .token(jwtToken)
//                .build();
        var user = User.builder().username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN).build();
        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        return AuthenticationResponse.builder().token(jwt).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );//if not correct an exception will be thrown, if correct generate token and send it back
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        var jwtToken= jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    @Override
    public JwtAuthenticationResponse signup(SignUpRequest request) {
        var user = User.builder().username(request.getUsername())
                .email(request.getEmail()).password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER).build();
        userRepository.save(user);
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    @Override
    public JwtAuthenticationResponse signin(SigninRequest request) {
//        System.out.println("I AM IN SIGN IN!!!");
//        System.out.println("____________________________________:::::");
//        System.out.println(request.getUsername());
//        System.out.println("____________________________________:::::");
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        var jwt = jwtService.generateToken(user);
        return JwtAuthenticationResponse.builder().token(jwt).build();
    }

    @Override
    public String changeRole(long userId, String role) {
        var user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found."));
        user.setRole(Role.valueOf(role.toUpperCase()));
        userRepository.save(user);
        return "Role changed successfully.";
    }
}
