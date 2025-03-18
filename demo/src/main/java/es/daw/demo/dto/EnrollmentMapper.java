package es.daw.demo.DTO;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.daw.demo.model.Enrollment;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {
    EnrollmentDTO toDTO(Enrollment enrollment);
    List<EnrollmentDTO> toDTO(List<Enrollment> enrollments);
    
    Enrollment toDomain(EnrollmentDTO enrollmentDTO);
}

