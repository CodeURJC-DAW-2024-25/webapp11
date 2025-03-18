package es.daw.demo.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.daw.demo.model.Course;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseDTO toDTO(Course course);
    List<CourseDTO> toDTOs(Collection<Course> courses);

    @Mapping(target = "imageFile", ignore = true)
    @Mapping(target = "notes", ignore = true)
    Course toDomain(CourseDTO courseDTO);


}

