package es.daw.demo.DTO;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.daw.demo.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    List<UserDTO> toDTO(Collection<User> users);

    @Mapping(target = "profileImage", ignore = true)
    @Mapping(target = "taughtCourses", ignore = true)
    User toDomain(UserDTO userDTO);
}
