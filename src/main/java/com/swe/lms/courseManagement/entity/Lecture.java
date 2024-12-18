package com.swe.lms.courseManagement.entity;
import com.swe.lms.userManagement.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="Lectures")
public class Lecture {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Date", nullable = false)
    private LocalDateTime lectureDate;

    @OneToMany
    private List<User> attendanceList;

    @Column(name="Course id")
    private Long course_id;

    @Column(name="OTP")
    private String OTP;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

//    public void applyOTP(User user, String OTP){
//        if(OTP.equals(this.OTP) && user.getRole().equals("Student") && !attendanceList.contains(user)){
//            attendanceList.add(user);
//        }
//    }
}
