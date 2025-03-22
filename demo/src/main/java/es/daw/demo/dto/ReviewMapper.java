package es.daw.demo.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.daw.demo.model.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    @Mapping(target = "parent", ignore = true)
    ReviewDTO toDTO(Review review);

    List<ReviewDTO> toDTOs(Collection<Review> reviews);
    
    Review toDomain(ReviewDTO reviewDTO);
}
