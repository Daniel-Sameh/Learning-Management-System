package com.swe.lms.userManagement.controller;

import com.swe.lms.security.dao.request.RoleRequest;
import com.swe.lms.userManagement.Service.AuthenticationService;
import com.swe.lms.userManagement.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AuthenticationService authenticationService;
    @PutMapping("/role/change/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> changeRole(@PathVariable("userId") long userId, @RequestBody RoleRequest role){
        System.out.println("____________________________________:::::");
        System.out.println(userId);
        System.out.println(role.getRole());
        System.out.println("____________________________________:::::");
        return ResponseEntity.ok(authenticationService.changeRole(userId, role.getRole()));
    }

    @DeleteMapping("/delete/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") long userId){
        return ResponseEntity.ok(authenticationService.deleteUser(userId));
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<User>> getUsers(){
        return ResponseEntity.ok(authenticationService.getUsers());
    }

}
