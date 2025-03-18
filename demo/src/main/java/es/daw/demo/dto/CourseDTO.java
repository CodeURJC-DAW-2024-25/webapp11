package es.daw.demo.dto;
 
public record CourseDTO(
        Long id,
        String title,
        String description,
        String topic,
        UserDTO instructor,
        int rating) {
}