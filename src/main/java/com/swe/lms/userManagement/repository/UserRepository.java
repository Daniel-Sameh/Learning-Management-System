package com.swe.lms.userManagement.repository;

import com.swe.lms.userManagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
