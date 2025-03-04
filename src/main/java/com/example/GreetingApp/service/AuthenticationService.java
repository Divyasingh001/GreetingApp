package com.example.GreetingApp.service;
import com.example.GreetingApp.DTO.AuthUserDTO;
import com.example.GreetingApp.Exception.UserException;

import com.example.GreetingApp.model.AuthUser;
import com.example.GreetingApp.repository.AuthUserRepository;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService  {
    @Autowired
    AuthUserRepository authUserRepository;

    public AuthUser register(AuthUserDTO userDTO) throws Exception {
            AuthUser user = new AuthUser(userDTO);
            System.out.println(user);
            authUserRepository.save(user);
            return user;
    }

    }


