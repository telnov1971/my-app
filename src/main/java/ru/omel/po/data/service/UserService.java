package ru.omel.po.data.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import ru.omel.po.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.vaadin.artur.helpers.CrudService;

import java.util.Optional;

@Service
public class UserService extends CrudService<User, Long> implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository
            , PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected JpaRepository<User, Long> getRepository() {
        return userRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findByUsername(s);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public Optional<User> findById(Long aLong) {
        return userRepository.findById(aLong);
    }
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    public void activate(String code) {
        User user = userRepository.findByActivationCode(code);
        try {
            user.setActive(true);
            user.setActivationCode("");
            userRepository.save(user);
        } catch(Exception ignored) {
        }
    }

    public void reset(String code) {
        User user = userRepository.findByUsername(code);
        try {
            user.setPassword(this.passwordEncoder.encode("1234"));
            userRepository.save(user);
            userRepository.flush();
        } catch(Exception ignored) {
        }
    }
}
