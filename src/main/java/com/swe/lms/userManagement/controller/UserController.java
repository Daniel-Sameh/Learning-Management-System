package com.swe.lms.userManagement.controller;

import com.swe.lms.userManagement.Service.UserService;
import com.swe.lms.userManagement.dto.UserDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserController {
    private UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> CreateUser(@RequestBody UserDto userDto){
        UserDto savedUser = userService.CreateUser(userDto);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
}
