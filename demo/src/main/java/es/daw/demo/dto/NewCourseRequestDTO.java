package es.daw.demo.dto;

import org.springframework.web.multipart.MultipartFile;

public record NewCourseRequestDTO (
        String title,
        String description,
        String topic,
        MultipartFile imageFile,
        MultipartFile notes){
}