package com.tan.quiz.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tan.quiz.model.User;
import com.tan.quiz.model.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepo;
    //Autowired
    //PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findById(username).orElse(null);

        if (user == null) {
            throw new UsernameNotFoundException("Not found: " + username);
        }

        return new UserDetailsImpl(user);
    }

    public void saveUser(User user) {
        User tmpUser = new User();
        tmpUser.setEmail(user.getEmail());
        tmpUser.setPassword((new BCryptPasswordEncoder()).encode(user.getPassword()));
        userRepo.save(tmpUser);
    }

    public boolean containsId(String email) {
        return userRepo.existsById(email);
    }

}
