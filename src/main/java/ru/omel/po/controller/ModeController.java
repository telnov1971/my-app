package ru.omel.po.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ru.omel.po.config.AppEnv;

@Controller
public class ModeController {
    @Autowired

    @GetMapping("/mode/on")
    public String on() {
        AppEnv.setNewMode(true);
        return "redirect:/";
    }
    @GetMapping("/mode/off")
    public String off() {
        AppEnv.setNewMode(true);
        return "redirect:/";
    }
  
}
