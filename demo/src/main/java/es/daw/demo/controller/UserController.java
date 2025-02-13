package es.daw.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import es.daw.demo.repository.UserRepository;
import es.daw.demo.model.User;
import java.util.Optional;


import jakarta.servlet.http.HttpSession;

public class UserController {
    
    @Autowired 
    private UserRepository userRepository;

    // Create a new user
    @GetMapping("/newUser")
    public String newUser(@RequestParam String firstName,
                          @RequestParam String lastName,
                          @RequestParam String email,
                          @RequestParam MultipartFile profileImage,
                          @RequestParam String topic,
                          @RequestParam String password,
                          @RequestParam String repeatPassword,
                          Model model
                        ) {
        // Check if the passwords match
        if (!password.equals(repeatPassword)) {
            model.addAttribute("errorTitle", "Error al crear la cuenta");
            model.addAttribute("errorMessage", "Las contraseñas no coinciden");
            return "error";
        }

        // Check if the user already exists
        /*if (userRepository.findByEmail(email) != null) {
            model.addAttribute("errorTitle", "Error al crear la cuenta");
            model.addAttribute("errorMessage", "El email ya está en uso");
            return "error";
        }*/

        // Save the user
        






        return "index";
    }


    // Find a user by email and Password
    @PostMapping("/findUser")
    public String findUser(@RequestParam String email,
                           @RequestParam String password,
                           HttpSession session,
                           Model model) {
        User user = userRepository.findByEmail(email);
        if (user == null || !user.getPassword().equals(password)) {
            model.addAttribute("errorTitle", "Error al iniciar sesión");
            model.addAttribute("errorMessage", "Email o contraseña incorrectos");
            return "error";
        }
        session.setAttribute("user", user);
        return "index";
    }


    //Edit user
    @PostMapping("/editUser")
    public String editUser (Model model, User editedUser) {
        Optional<User> userOptional = userRepository.findById(editedUser.getId());
        if (userOptional.isPresent()) {
            userRepository.save(editedUser);
            model.addAttribute("user", editedUser);
            return "profile";
        } else {
            model.addAttribute("errorTitle", "Error al editar el usuario");
            model.addAttribute("errorMessage", "El usuario no existe");
            return "error";
        }
    }

    //Delete user
    @PostMapping("/deleteUser")
    public String deleteUser (Model model, User deletedUser) {
        Optional<User> userOptional = userRepository.findById(deletedUser.getId());
        if (userOptional.isPresent()) {
            userRepository.delete(deletedUser);
            model.addAttribute("errorTitle", "Usuario eliminado");
            model.addAttribute("errorMessage", "El usuario ha sido eliminado");
            return "error";
        } else {
            model.addAttribute("errorTitle", "Error al eliminar el usuario");
            model.addAttribute("errorMessage", "El usuario no existe");
            return "error";
        }
    }
}
