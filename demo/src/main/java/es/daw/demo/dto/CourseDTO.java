package es.daw.demo.dto;

import java.util.List;

public record CourseDTO(
        Long id,
        String title,
        String description,
        String topic,
        UserDTO instructor,
        int rating) {
}
