package es.daw.demo.DTO;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.daw.demo.model.Course;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseDTO toDTO(Course course);
    List<CourseDTO> toDTO(List<Course> courses);

    @Mapping(target = "imageFile", ignore = true)
    @Mapping(target = "notes", ignore = true)
    Course toDomain(CourseDTO courseDTO);

    
}

