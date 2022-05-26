package ru.omel.po.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.omel.po.data.service.UserService;

@Controller
public class UserReset {
    @Autowired
    private UserService userService;

    @GetMapping("/reset/{user}")
    public String reset(@PathVariable String user){
        userService.reset(user);
        return "redirect:/";
    }
}
