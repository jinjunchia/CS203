package com.cs203.cs203system.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    // Display the custom login page (if you have one)
    @GetMapping("/login")
    public String login() {
        return "login";  // This returns a view (like a login.html or login.jsp)
    }

    // Handle login failure
    @GetMapping("/login-error")
    public String loginError() {
        return "login-error";  // This returns a view showing login failure
    }

    // You can handle successful login using custom logic (optional)
    @PostMapping("/login")
    public String postLogin(@RequestParam String username, @RequestParam String password) {
        // This is where you can handle custom login logic (if necessary).
        // Normally, Spring Security handles the login logic for you automatically.
        return "redirect:/home";  // Redirect to the home page after successful login
    }
}
