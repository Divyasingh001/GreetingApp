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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class AuthenticationService implements IAuthenticationService {

    @Autowired
    AuthUserRepository authUserRepository;

    @Autowired
    jwttoken tokenUtil;

    @Autowired
    EmailSenderService emailSenderService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public AuthUser register(AuthUserDTO userDTO) throws Exception {
        log.info("Starting registration process for user: {}", userDTO.getEmail());

        AuthUser user = new AuthUser(userDTO);
        String encryptedPassword = passwordEncoder.encode(userDTO.getPassword());
        user.setPassword(encryptedPassword);

        String token = tokenUtil.createToken(user.getUserId());
        authUserRepository.save(user);

        // Cache the user details in Redis
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set("USER_" + user.getEmail(), user, 10, TimeUnit.MINUTES);

        emailSenderService.sendEmail(user.getEmail(), "Registered in Greeting App", "Hi "
                + user.getFirstName() + ",\nYou have been successfully registered!\n\nYour token: " + token);

        log.info("User registered successfully: {}", user.getEmail());
        return user;
    }

    @Override
    @Cacheable(value = "users", key = "#loginDTO.email", unless = "#result == null")
    public String login(LoginDTO loginDTO) {
        log.info("Attempting login for user: {}", loginDTO.getEmail());

        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        AuthUser cachedUser = (AuthUser) ops.get("USER_" + loginDTO.getEmail());

        Optional<AuthUser> user = Optional.ofNullable(
                cachedUser != null ? cachedUser : authUserRepository.findByEmail(loginDTO.getEmail()));

        if (user.isPresent()) {
            if (passwordEncoder.matches(loginDTO.getPassword(), user.get().getPassword())) {
                emailSenderService.sendEmail(user.get().getEmail(), "Logged in Successfully!", "Hi "
                        + user.get().getFirstName() + ",\n\nYou have successfully logged in!");

                log.info("Login successful for user: {}", loginDTO.getEmail());

                // Store the login session token in Redis
                String token = tokenUtil.createToken(user.get().getUserId());
                ops.set("TOKEN_" + loginDTO.getEmail(), token, 30, TimeUnit.MINUTES);

                return "Congratulations!! You have logged in successfully!";
            } else {
                log.warn("Incorrect password for user: {}", loginDTO.getEmail());
                throw new UserException("Sorry! Email or Password is incorrect!");
            }
        } else {
            log.warn("User not found for email: {}", loginDTO.getEmail());
            throw new UserException("Sorry! Email or Password is incorrect!");
        }
    }

    @Override
    @CacheEvict(value = "users", key = "#email")
    public String forgotPassword(String email, String newPassword) {
        log.info("Processing forgot password request for user: {}", email);
        AuthUser user = authUserRepository.findByEmail(email);

        if (user == null) {
            log.error("User not found for email: {}", email);
            throw new UserException("Sorry! We cannot find the user email: " + email);
        }

        String encryptedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encryptedPassword);
        authUserRepository.save(user);

        emailSenderService.sendEmail(user.getEmail(),
                "Password Reset Successful",
                "Hi " + user.getFirstName() + ",\n\nYour password has been successfully changed!");

        // Remove the old user cache
        redisTemplate.delete("USER_" + email);

        log.info("Password changed successfully for user: {}", email);
        return "Password has been changed successfully!";
    }

    @Override
    @CacheEvict(value = "users", key = "#email")
    public String resetPassword(String email, String currentPassword, String newPassword) {
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

        // Remove the old user cache
        redisTemplate.delete("USER_" + email);

        log.info("Password reset successfully for user: {}", email);
        return "Password reset successfully!";
    }

    public String logout(String email) {
        log.info("Logging out user: {}", email);
        redisTemplate.delete("TOKEN_" + email);
        return "User logged out successfully!";
    }
}
