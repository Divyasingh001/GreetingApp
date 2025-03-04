package com.example.GreetingApp.service;
import com.example.GreetingApp.DTO.AuthUserDTO;

import com.example.GreetingApp.model.AuthUser;

public interface IAuthenticationService {
    AuthUser register(AuthUserDTO userDTO) throws Exception;

}
