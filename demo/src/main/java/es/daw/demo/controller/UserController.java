package es.daw.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;


import jakarta.servlet.http.HttpSession;

public class UserController {
    
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

}
