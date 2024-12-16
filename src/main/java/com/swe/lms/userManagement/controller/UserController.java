package com.swe.lms.userManagement.controller;

import com.swe.lms.security.dao.request.RoleRequest;
import com.swe.lms.security.dao.request.SignUpRequest;
import com.swe.lms.security.dao.request.SigninRequest;
import com.swe.lms.security.dao.response.JwtAuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<JwtAuthenticationResponse> register(@RequestBody SignUpRequest request){
        return  ResponseEntity.ok(authenticationService.signup(request));
    }
    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> register(@RequestBody SigninRequest request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("____________________________________:::::");
        System.out.println(authentication);
        System.out.println("____________________________________:::::");
        return  ResponseEntity.ok(authenticationService.signin(request));
    }

    @PutMapping("admin/change_role/{userId}")
    public ResponseEntity<String> changeRole(@PathVariable("userId") long userId, @RequestBody RoleRequest role){
        System.out.println("____________________________________:::::");
        System.out.println(userId);
        System.out.println(role.getRole());
        System.out.println("____________________________________:::::");
        return ResponseEntity.ok(authenticationService.changeRole(userId, role.getRole()));
    }

//    @PostMapping("/register")
//    public ResponseEntity<String> CreateUser(@RequestBody UserDto user){
//        String response = String.valueOf(userService.CreateUser(user));
//        return ResponseEntity.status(HttpStatus.CREATED).body(response);
//    }
//    @PostMapping("/generateToken")
//    public ResponseEntity<String> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
//        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
//        if (authentication.isAuthenticated()) {
//            String token = jwtService.generateToken(authRequest.getUsername());
//            return ResponseEntity.ok(token);
//        } else {
//            throw new UsernameNotFoundException("Invalid user request!");
//        }
//    }
//
//    @GetMapping("/user/{userid}")
//    public ResponseEntity<UserDto> GetUserById(@PathVariable("userid") long userid){
//        UserDto user = userService.GetUserById(userid);
//        return new ResponseEntity<>(user, HttpStatus.OK);
//    }
}
