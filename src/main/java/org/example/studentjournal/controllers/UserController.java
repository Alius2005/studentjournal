package org.example.studentjournal.controllers;

import org.example.studentjournal.POJO.User;
import org.example.studentjournal.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public String listUsers(Model model) {
        try {
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            return "users/list";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to load users: " + e.getMessage());
            return "error";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public String getUser(@PathVariable int id, Model model) {
        try {
            Optional<User> user = userService.getUserById(id);
            if (user.isPresent()) {
                model.addAttribute("user", user.get());
            } else {
                model.addAttribute("error", "User not found");
            }
            return "users/form";
        } catch (Exception e) {
            model.addAttribute("error", "User not found: " + e.getMessage());
            return "error";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/new")
    public String newUserForm(Model model) {
        model.addAttribute("user", new User());
        return "users/form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public String saveUser(@ModelAttribute User user, Model model) {
        try {
            if (user.getPasswordHash() != null && !user.getPasswordHash().isEmpty()) {
                user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
            }
            userService.saveUser(user);
            return "redirect:/users";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to save user: " + e.getMessage());
            model.addAttribute("user", user);
            return "users/form";
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/delete/{id}")
    public String deleteUser(@PathVariable int id, Model model) {
        try {
            userService.deleteUser(id);
            return "redirect:/users";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to delete user: " + e.getMessage());
            return "redirect:/users";
        }
    }
}
