package com.example.application.service;

import com.example.application.jwt.AuthenticationRequest;
import com.example.application.jwt.AuthenticationResponse;
import com.example.application.jwt.JwtUtil;
import com.example.application.jwt.MyUserDetailsService;
import com.example.application.model.User;
import com.example.application.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Optional<User> findByEmail(String email) {
        return userRepository.findByMail(email);
    }

    public void save(User user) {
        if (userRepository.existsByMail(user.getMail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        userRepository.save(user);
    }

    public User authenticate(String email, String password) throws Exception {
        Optional<User> userOpt = userRepository.findByMail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (BCrypt.checkpw(password, user.getPassword())) {
                return user;
            } else {
                throw new Exception("Incorrect email or password");
            }
        } else {
            throw new Exception("User not found");
        }
    }
}