package ru.omel.po.controller;

import ru.omel.po.data.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/activate/{code}")
    public String activate(@PathVariable String code){
        userService.activate(code);
        return "redirect:/";
    }
}
