package com.example.GreetingApp.Interface;
import com.example.GreetingApp.DTO.AuthUserDTO;
import com.example.GreetingApp.DTO.LoginDTO;
import com.example.GreetingApp.model.AuthUser;


public interface IAuthenticationService {
    AuthUser register(AuthUserDTO userDTO) throws Exception;

    String login(LoginDTO loginDTO);
    String forgotPassword(String email, String newPassword);
    String resetPassword(String email, String currentPassword, String newPassword);
}
