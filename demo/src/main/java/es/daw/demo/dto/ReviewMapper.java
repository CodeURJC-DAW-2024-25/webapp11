package es.daw.demo.DTO;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import es.daw.demo.model.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    
    ReviewDTO toDTO(Review review);

    List<ReviewDTO> toDTOs(Collection<Review> reviews);
    
    Review toDomain(ReviewDTO reviewDTO);
}
