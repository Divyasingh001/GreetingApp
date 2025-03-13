package com.example.GreetingApp.service;

import com.example.GreetingApp.DTO.AuthUserDTO;
import com.example.GreetingApp.DTO.LoginDTO;
import com.example.GreetingApp.Exception.UserException;
import com.example.GreetingApp.Interface.IAuthenticationService;
import com.example.GreetingApp.Util.EmailSenderService;
import com.example.GreetingApp.Util.jwttoken;
import com.example.GreetingApp.model.AuthUser;
import com.example.GreetingApp.repository.AuthUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AuthenticationService implements IAuthenticationService {

    @Autowired
    AuthUserRepository authUserRepository;

    @Autowired
    jwttoken tokenUtil;

    @Autowired
    EmailSenderService emailSenderService;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public AuthUser register(AuthUserDTO userDTO) throws Exception {
        try {
            log.info("Starting registration process for user: {}", userDTO.getEmail());

            // Check if user already exists
            if (authUserRepository.findByEmail(userDTO.getEmail()) != null) {
                log.warn("User already exists with email: {}", userDTO.getEmail());
                throw new UserException("User already exists with email: " + userDTO.getEmail());
            }

            AuthUser user = new AuthUser(userDTO);
            String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
            user.setPassword(encryptedPassword);

            String token = tokenUtil.createToken(user.getUserId());
            authUserRepository.save(user);

            emailSenderService.sendEmail(user.getEmail(), "Registered in Greeting App", "Hi "
                    + user.getFirstName() + ",\nYou have been successfully registered!\n\nYour registered details are:\n\n User Id:  "
                    + user.getUserId() + "\n First Name:  "
                    + user.getFirstName() + "\n Last Name:  "
                    + user.getLastName() + "\n Email:  "
                    + user.getEmail() + "\n Token:  " + token);

            log.info("User registered successfully: {}", user.getEmail());
            return user;
        } catch (Exception e) {
            log.error("Registration failed for user: {}, Error: {}", userDTO.getEmail(), e.getMessage());
            throw new UserException("Registration failed: " + e.getMessage());
        }
    }

    @Override
    public String login(LoginDTO loginDTO) {
        try {
            log.info("Attempting login for user: {}", loginDTO.getEmail());
            Optional<AuthUser> user = Optional.ofNullable(authUserRepository.findByEmail(loginDTO.getEmail()));

            if (user.isEmpty()) {
                log.warn("User not found for email: {}", loginDTO.getEmail());
                throw new UserException("Invalid email or password.");
            }

            if (!passwordEncoder.matches(loginDTO.getPassword(), user.get().getPassword())) {
                log.warn("Incorrect password for user: {}", loginDTO.getEmail());
                throw new UserException("Invalid email or password.");
            }

            emailSenderService.sendEmail(user.get().getEmail(), "Logged in Successfully!", "Hi "
                    + user.get().getFirstName() + ",\n\nYou have successfully logged in to the Greeting App!");

            log.info("Login successful for user: {}", loginDTO.getEmail());
            return "Congratulations!! You have logged in successfully!";
        } catch (Exception e) {
            log.error("Login failed for user: {}, Error: {}", loginDTO.getEmail(), e.getMessage());
            throw new UserException("Login failed: " + e.getMessage());
        }
    }

    @Override
    public String forgotPassword(String email, String newPassword) {
        try {
            log.info("Processing forgot password request for user: {}", email);
            AuthUser user = authUserRepository.findByEmail(email);

            if (user == null) {
                log.error("User not found for email: {}", email);
                throw new UserException("User not found with email: " + email);
            }

            String encryptedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encryptedPassword);
            authUserRepository.save(user);

            emailSenderService.sendEmail(user.getEmail(),
                    "Password Reset Successful",
                    "Hi " + user.getFirstName() + ",\n\nYour password has been successfully changed!");

            log.info("Password changed successfully for user: {}", email);
            return "Password has been changed successfully!";
        } catch (Exception e) {
            log.error("Forgot password request failed for user: {}, Error: {}", email, e.getMessage());
            throw new UserException("Password reset failed: " + e.getMessage());
        }
    }

    @Override
    public String resetPassword(String email, String currentPassword, String newPassword) {
        try {
            log.info("Processing reset password request for user: {}", email);
            AuthUser user = authUserRepository.findByEmail(email);

            if (user == null) {
                log.error("User not found for email: {}", email);
                throw new UserException("User not found with email: " + email);
            }

            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                log.warn("Incorrect current password for user: {}", email);
                throw new UserException("Current password is incorrect!");
            }

            String encryptedPassword = passwordEncoder.encode(newPassword);
            user.setPassword(encryptedPassword);
            authUserRepository.save(user);

            emailSenderService.sendEmail(user.getEmail(),
                    "Password Reset Successful",
                    "Hi " + user.getFirstName() + ",\n\nYour password has been successfully updated!");

            log.info("Password reset successfully for user: {}", email);
            return "Password reset successfully!";
        } catch (Exception e) {
            log.error("Reset password request failed for user: {}, Error: {}", email, e.getMessage());
            throw new UserException("Password reset failed: " + e.getMessage());
        }
    }
}
