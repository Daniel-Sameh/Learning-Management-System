package com.swe.lms.userManagement.controller;

import com.swe.lms.security.JwtService;
import com.swe.lms.userManagement.Service.UserInfoService;
import com.swe.lms.userManagement.dto.UserDto;
import com.swe.lms.userManagement.entity.AuthRequest;
import com.swe.lms.userManagement.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class UserController {
    private UserInfoService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserController(UserInfoService userService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }



    @PostMapping("/register")
    public ResponseEntity<String> CreateUser(@RequestBody UserDto user){
        String response = String.valueOf(userService.CreateUser(user));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @PostMapping("/generateToken")
    public ResponseEntity<String> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            String token = jwtService.generateToken(authRequest.getUsername());
            return ResponseEntity.ok(token);
        } else {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

    @GetMapping("/user/{userid}")
    public ResponseEntity<UserDto> GetUserById(@PathVariable("userid") long userid){
        UserDto user = userService.GetUserById(userid);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }
}
