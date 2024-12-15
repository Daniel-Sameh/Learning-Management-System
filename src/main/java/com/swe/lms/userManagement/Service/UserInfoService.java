package com.swe.lms.userManagement.Service;

import com.swe.lms.userManagement.Exception.ResourceNotFoundException;
import com.swe.lms.userManagement.dto.UserDto;

import com.swe.lms.userManagement.entity.User;
import com.swe.lms.userManagement.mapper.UserMapper;
import com.swe.lms.userManagement.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserInfoService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    public UserInfoService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }
    public UserDto CreateUser(UserDto userDto){
        User user=UserMapper.mapToUser(userDto);
        user.setPassword(encoder.encode(userDto.getPassword()));
        userRepository.save(user);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto GetUserById(long userid){
        User user = userRepository.findById(userid)
                    .orElseThrow(()-> new ResourceNotFoundException("no user exist with given id" + userid));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(UserInfoDetails::new).orElseThrow(()-> new UsernameNotFoundException("User not found with username: " + username));
    }
}
