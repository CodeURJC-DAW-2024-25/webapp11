package es.daw.demo.dto;

import org.mapstruct.Mapper;

import es.daw.demo.model.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    
    ReviewDTO toDTO(Review review);

    
    Review toDomain(ReviewDTO reviewDTO);
}
