package com.swe.lms.courseManagement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseDTO {
    private String name;
    private String code;
    private String instructor;

    public CourseDTO( String name, String code, String instructor) {
        this.name = name;
        this.code = code;
        this.instructor = instructor;
    }




}
