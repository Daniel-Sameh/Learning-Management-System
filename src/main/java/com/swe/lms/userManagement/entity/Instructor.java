package com.swe.lms.userManagement.entity;

import com.swe.lms.userManagement.entity.User;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Table(name = "instructors")
public class Instructor extends User {

}
