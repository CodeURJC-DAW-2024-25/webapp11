package es.daw.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.core.io.Resource;
import es.daw.demo.service.EmailService;
import es.daw.demo.service.UserService;
import es.daw.demo.dto.UserDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestParam String firstName,
                                               @RequestParam String lastName,
                                               @RequestParam String email,
                                               @RequestParam MultipartFile profileImage,
                                               @RequestParam String topic,
                                               @RequestParam String password,
                                               @RequestParam String repeatPassword) throws Exception {
        if (!password.equals(repeatPassword)) {
            return ResponseEntity.badRequest().body("Las contraseñas no coinciden");
        }
        if (userService.existsByEmail(email)) {
            return ResponseEntity.badRequest().body("El usuario ya existe");
        }
        password = passwordEncoder.encode(password);
        UserDTO user = new UserDTO(null, firstName, lastName, email, topic, List.of("USER"));
        userService.createUser(user, profileImage, password);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado exitosamente");
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> getProfileImage(@PathVariable long id) throws SQLException {
        Resource profileImage = userService.getUserImage(id);
        return ResponseEntity.ok().body(profileImage);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> listUsers(@RequestParam(required = false) String name) {
        List<UserDTO> users = (name != null && !name.isEmpty()) ?
                (List<UserDTO>) userService.findByFirstNameContainingIgnoreCase(name) : (List<UserDTO>) userService.findAll();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        UserDTO user = userService.findById(id);
        if (user != null) {
            emailService.sendEmail(user.email(), "Cuenta eliminada", "Tu cuenta ha sido eliminada");
            userService.deleteById(id);
            return ResponseEntity.ok("Usuario eliminado exitosamente");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUser(@PathVariable Long id,
                                             @RequestParam String firstName,
                                             @RequestParam String lastName,
                                             @RequestParam String email,
                                             @RequestParam String topic,
                                             @RequestParam String currentPassword,
                                             @RequestParam String newPassword,
                                             @RequestParam String confirmPassword,
                                             @RequestParam(required = false) MultipartFile imageFile) throws IOException, SQLException {
        UserDTO user = userService.findById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body("Las contraseñas no coinciden");
        }
        String password = passwordEncoder.encode(newPassword);
        userService.updateUser(id, firstName, lastName, email, topic, imageFile, password);
        return ResponseEntity.ok("Usuario actualizado exitosamente");
    }
}
