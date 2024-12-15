package com.swe.lms.courseManagement.entity;
import com.swe.lms.userManagement.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


//@Getter
//@Setter
//@AllArgsConstructor
//@NoArgsConstructor
@Table(name="Lectures")
public class Lecture {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @OneToMany
    private List<User> attendanceList;

    @Column(name="Course id")
    private Long course_id;

    @Column(name="OTP", nullable=false)
    private String OTP;

    public Lecture(Long id, List<User> attendanceList, Long course_id, String OTP) {
        this.id = id;
        this.attendanceList = attendanceList;
        this.course_id = course_id;
        this.OTP = OTP;
    }

    public Long getId() {
        return id;
    }

    public List<User> getAttendanceList() {
        return attendanceList;
    }

    public Long getCourse_id() {
        return course_id;
    }

    public String getOTP() {
        return OTP;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAttendanceList(List<User> attendanceList) {
        this.attendanceList = attendanceList;
    }

    public void setCourse_id(Long course_id) {
        this.course_id = course_id;
    }

    public void setOTP(String OTP) {
        this.OTP = OTP;
    }


    public void applyOTP(User user, String OTP){
        if(OTP.equals(this.OTP) && user.getRole().equals("Student") && !attendanceList.contains(user)){
            attendanceList.add(user);
        }
    }
}
