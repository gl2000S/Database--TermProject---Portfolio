package com.cinetrack.controller;

import com.cinetrack.model.User;
import com.cinetrack.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/signup")
    public String showSignupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String username,
                         @RequestParam String email,
                         @RequestParam String password,
                         @RequestParam(required = false) String bio,
                         Model model) {

        boolean created = authService.registerUser(username, email, password, bio);

        if (!created) {
            model.addAttribute("error", "Signup failed. Username or email may already be taken.");
            return "signup";
        }

        model.addAttribute("success", "Account created successfully. Please log in.");
        return "login";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String usernameOrEmail,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {

        User user = authService.loginUser(usernameOrEmail, password);

        if (user == null) {
            model.addAttribute("error", "Invalid username/email or password.");
            return "login";
        }

        session.setAttribute("loggedInUser", user);
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("username", user.getUsername());

        return "redirect:/home";
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/home";
    }
}
