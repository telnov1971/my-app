package com.example.application.data.service;

import com.example.application.data.entity.Role;
import com.example.application.data.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.UUID;

public class UserRegistration {
    @Autowired
    private static PasswordEncoder passwordEncoder;
    @Autowired
    private static UserRepository userRepository;


    public static User addUser(User user, String host) {
        String username = user.getUsername();
        User userFromDb = userRepository.findByUsername(username);
        if (userFromDb == null) {
            user.setActive(false);
            user.setRoles(Collections.singleton(Role.USER));
            user.setActivationCode(UUID.randomUUID().toString());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            try {
                userRepository.save(user);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            //sendMessage(user, host);
        }
        return user;
    }
}
