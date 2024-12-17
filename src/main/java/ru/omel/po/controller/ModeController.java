package ru.omel.po.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ru.omel.po.config.AppEnv;
import ru.omel.po.data.service.UserService;
import ru.omel.po.data.entity.Role;
import ru.omel.po.data.entity.User;

@Controller
public class ModeController {
    @Autowired
    private UserService userService;

    @GetMapping("/mode/on")
    public String on() {
        User currentUser;

        currentUser = userService.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName());
        if(currentUser.getRoles().contains(Role.ADMIN))
            AppEnv.setNewMode(true);
        return "redirect:/";
    }
    @GetMapping("/mode/off")
    public String off() {
        User currentUser;

        currentUser = userService.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName());
        if(currentUser.getRoles().contains(Role.ADMIN))
            AppEnv.setNewMode(false);
        return "redirect:/";
    }

    @GetMapping("/mode/")
    public String mode() {
        User currentUser;

        currentUser = userService.findByUsername(
                SecurityContextHolder.getContext().getAuthentication().getName());
        if(currentUser.getRoles().contains(Role.ADMIN))
            System.out.println(AppEnv.getNewMode());
        return "redirect:/";
    }

}
