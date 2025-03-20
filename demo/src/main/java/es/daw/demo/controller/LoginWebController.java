package es.daw.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginWebController {
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/error-login")
    public String loginError(Model model) {
        model.addAttribute("errorTitle", "Error de Autenticación");
        model.addAttribute("errorMessage", "Usuario o contraseña incorrectos");
        return "/error";
    }
}
