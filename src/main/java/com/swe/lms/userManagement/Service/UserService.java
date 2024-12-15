package com.swe.lms.userManagement.Service;

import com.swe.lms.userManagement.dto.UserDto;

public interface UserService {
    UserDto CreateUser(UserDto userDto);
    UserDto GetUserById(long userid);

}
