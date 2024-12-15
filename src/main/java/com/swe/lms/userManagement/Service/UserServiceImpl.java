package com.swe.lms.userManagement.Service;

import com.swe.lms.userManagement.Exception.ResourceNotFoundException;
import com.swe.lms.userManagement.dto.UserDto;

import com.swe.lms.userManagement.entity.User;
import com.swe.lms.userManagement.mapper.UserMapper;
import com.swe.lms.userManagement.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService{
    private UserRepository userRepository;
    @Override
    public UserDto CreateUser(UserDto userDto){
        User user=UserMapper.mapToUser(userDto);
        User savedUser=userRepository.save(user);
        return UserMapper.mapToUserDto(savedUser);
    }
    @Override
    public UserDto GetUserById(long userid){
        userRepository.findById(userid)
                .orElseThrow(()-> new ResourceNotFoundException("no user exist with given id" + userid));
        return null;
    }
}
