package es.daw.demo.controller;

import es.daw.demo.service.EmailService;
import es.daw.demo.service.UserService;
import es.daw.demo.model.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class RESTUserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Operation(summary = "Obtener lista de usuarios paginados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            }),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content)
    })
    @GetMapping("/")
    public Page<User> getUsers(Pageable page) {
        page = PageRequest.of(page.getPageNumber(), 5);
        return (Page<User>) userService.findAll();
    }

    @Operation(summary = "Obtener un usuario por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            }),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> userOptional = userService.findById(id);
        return userOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Buscar usuarios por nombre")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuarios encontrados", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            }),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida", content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsersByName(@RequestParam String name) {
        List<User> users = userService.findByFirstNameContainingIgnoreCase(name);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Crear un nuevo usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado correctamente", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            }),
            @ApiResponse(responseCode = "409", description = "El usuario ya existe", content = @Content)
    })
    @PostMapping("/")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        if (userService.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El usuario ya existe");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // user.setRoles("USER");
        user.setRoles(Collections.singletonList("USER"));
        userService.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @Operation(summary = "Actualizar un usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            }),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (updatedUser.getFirstName() != null && !updatedUser.getFirstName().isEmpty()) {
                user.setFirstName(updatedUser.getFirstName());
            }
            if (updatedUser.getLastName() != null && !updatedUser.getLastName().isEmpty()) {
                user.setLastName(updatedUser.getLastName());
            }
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
            }

            userService.save(user);
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }

    @Operation(summary = "Eliminar un usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Enviar correo de notificación
            String subject = "Tu cuenta ha sido eliminada";
            String message = "Estimado usuario,\n\nTu cuenta ha sido eliminada permanentemente.";
            emailService.sendEmail(user.getEmail(), subject, message);

            userService.deleteById(id);
            return ResponseEntity.ok("Usuario eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }
}
