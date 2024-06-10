package com.example.application.jwt;

import com.example.application.model.User;
import com.example.application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByMail(mail);
        User user = userOptional.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + mail));

        return new org.springframework.security.core.userdetails.User(user.getMail(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority("USER")));
    }
}