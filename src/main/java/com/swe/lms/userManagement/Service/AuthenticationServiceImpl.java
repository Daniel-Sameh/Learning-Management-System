package com.swe.lms.userManagement.Service;

import com.swe.lms.exception.ResourceNotFoundException;
import com.swe.lms.security.JwtService;
import com.swe.lms.security.dao.request.SignUpRequest;
import com.swe.lms.security.dao.request.SigninRequest;
import com.swe.lms.security.dao.response.JwtAuthenticationResponse;
import com.swe.lms.userManagement.Service.AuthenticationService;
import com.swe.lms.userManagement.controller.AuthenticationRequest;
import com.swe.lms.userManagement.controller.AuthenticationResponse;
import com.swe.lms.userManagement.controller.RegisterRequest;
import com.swe.lms.userManagement.entity.User;
import com.swe.lms.userManagement.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.swe.lms.userManagement.entity.Role;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private static final String SECRET_KEY = "9D0EB6B1C2E1FAD0F53A248F6C3B5E4E2F6D8G3H1I0J7K4L1M9N2O3P5Q0R7S9T1U4V2W6X0Y3Z";



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
        if (!userRepository.findByUsername(request.getUsername()).isEmpty()){
            throw new IllegalArgumentException("Username is already in use");
        }
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
                .orElseThrow(() -> new IllegalArgumentException("Invalid UserName or Password."));
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

    @Override
    public String deleteUser(long userId) {
        userRepository.deleteById(userId);
        return "User deleted successfully.";
    }

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User not found."));
    }

    public User updateprofile(Map<String, Object>payload, String token,Long userid){
        String username;
        String role;
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token).getBody();
            username = claims.getSubject();
            role = (String) claims.get("role");
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token", e);
        }

        User userx = userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("User not found.")
        );
        User user = userRepository.findById(userid).orElseThrow(
                () -> new ResourceNotFoundException("User not found.")
        );
        if (!"admin".equalsIgnoreCase(role) && userx.getId()!=userid){
            throw new IllegalArgumentException("Access denied");
        }
        if (payload.containsKey("name")) {
            String newName = (String) payload.get("name");
            Optional<User> existingUserWithName = userRepository.findByUsername(newName);
            if (existingUserWithName.isPresent()) {
                throw new IllegalArgumentException("Name is already taken.");
            }
            user.setUsername(newName);
        }
        if (payload.containsKey("email")) {
            String newEmail = (String) payload.get("email");
            Optional<User> existingUserWithEmail = userRepository.findByEmail(newEmail);
            if (existingUserWithEmail.isPresent()) {
                throw new IllegalArgumentException("Email is already in use.");
            }
            user.setEmail(newEmail);
        }
        if(payload.containsKey("password")){
            user.setPassword(passwordEncoder.encode((String) payload.get("password")));
        }
        userRepository.save(user);
        return user;
    }
}
