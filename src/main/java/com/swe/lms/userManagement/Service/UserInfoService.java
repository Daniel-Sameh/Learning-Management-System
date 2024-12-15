package com.swe.lms.userManagement.Service;

import com.swe.lms.userManagement.repository.UserRepository;
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
