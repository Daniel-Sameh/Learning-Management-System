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
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "Date", nullable = false)
    private LocalDateTime lectureDate;
    @Column(name = "title", nullable = false)
    private String title;
//el content can be (pdf video sora text)
    @Column(name = "content")
    private String content;

    @Column(name = "media", nullable = true)
    private String media;
    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private Course course;
}
