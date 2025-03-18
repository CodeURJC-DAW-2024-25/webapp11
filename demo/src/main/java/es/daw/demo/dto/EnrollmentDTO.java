package es.daw.demo.dto;

import java.util.Date;

public record EnrollmentDTO(
        Long id,
        UserDTO user,
        CourseDTO course,  
        int rating,
        Date date) {
}
