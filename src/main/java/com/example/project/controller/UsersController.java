package com.example.project.controller;

import com.example.project.model.User;
import com.example.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/users")
public class UsersController {
    private UserService userService;

    @Autowired
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public String listUsers(ModelMap model) {
        List<User> users = userService.listUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/add")
    public String addPage(@ModelAttribute User user) {
        return "add";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute User user) {
        userService.add(user);
        return "redirect:/users";
    }

    @GetMapping("/edit")
    public String editPage(@RequestParam long id, ModelMap model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        return "edit";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute User user) {
        userService.edit(user);
        return "redirect:/users";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam long id) {
        userService.delete(userService.getById(id));
        return "redirect:/users";
    }
}
