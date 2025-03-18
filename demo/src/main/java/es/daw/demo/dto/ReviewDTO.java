package es.daw.demo.dto;
 
import java.util.List;
 
public record ReviewDTO(
        Long id,
        String text,
        Boolean pending,
        UserDTO user,
        CourseDTO course,
        List<ReviewDTO> sons,
        ReviewDTO parent) {
}