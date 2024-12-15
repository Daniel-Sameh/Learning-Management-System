package com.swe.lms.courseManagement.entity;

import jakarta.persistence.*;
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "title")
    private String title;
//el content can be (pdf video sora text)
    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id")
    private Course course;
}
