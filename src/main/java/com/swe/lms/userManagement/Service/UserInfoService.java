package com.swe.lms.userManagement.Service;

import com.swe.lms.userManagement.entity.User;
import com.swe.lms.userManagement.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserInfoService implements UserService {
    private final UserRepository userRepository;


    @Override
    public UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) {
                return userRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            }
        };
    }
    private static final String SECRET_KEY = "9D0EB6B1C2E1FAD0F53A248F6C3B5E4E2F6D8G3H1I0J7K4L1M9N2O3P5Q0R7S9T1U4V2W6X0Y3Z";

    public Optional<User> getUserFromToken(String token) {
        try {
            Claims claims = Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token).getBody();
            String username = claims.getSubject();
            return userRepository.findByUsername(username);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid token", e);
        }
    }


//    public UserDto CreateUser(UserDto userDto){
//        User user=UserMapper.mapToUser(userDto);
//        user.setPassword(encoder.encode(userDto.getPassword()));
//        userRepository.save(user);
//        return UserMapper.mapToUserDto(user);
//    }
//
//    public UserDto GetUserById(long userid){
//        User user = userRepository.findById(userid)
//                    .orElseThrow(()-> new ResourceNotFoundException("no user exist with given id" + userid));
//        return UserMapper.mapToUserDto(user);
//    }


}
