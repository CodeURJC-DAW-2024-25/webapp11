package es.daw.demo.dto;

import java.util.List;

public record UserDTO(
        Long id,
        String firstName,
        String lastName,
        String email,
        String topic,
        List <String> roles) {
}