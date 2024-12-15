package com.swe.lms.userManagement.dto;


import lombok.*;

@ToString
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String role;

    public UserDto(){}
    public UserDto(Long id, String username, String email, String password, String role){
        this.id = id;
        this.username=username;
        this.email=email;
        this.password=password;
        this.role=role;
    }
    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }


}
