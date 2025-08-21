package com.wizwrite.service;

import com.wizwrite.dto.AuthResponse;
import com.wizwrite.dto.ContentRequest;
import com.wizwrite.dto.ContentResponse;
import com.wizwrite.dto.LoginDto;
import com.wizwrite.dto.RegisterDto;
import com.wizwrite.entity.ContentHistory;
import com.wizwrite.entity.User;
import com.wizwrite.entity.UserCredit;
import com.wizwrite.repository.ContentHistoryRepository;
import com.wizwrite.repository.UserCreditRepository;
import com.wizwrite.repository.UserRepository;
import com.wizwrite.security.JwtService;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserCreditRepository userCreditRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, UserCreditRepository userCreditRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.userCreditRepository = userCreditRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterDto registerDto) {
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        userRepository.save(user);

        UserCredit userCredit = new UserCredit();
        userCredit.setUser(user);
        userCredit.setCreditsRemaining(100); // Beri kredit awal
        userCredit.setLastUpdated(LocalDateTime.now());
        userCreditRepository.save(userCredit);

        String jwtToken = jwtService.generateToken(new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Collections.emptyList()));

        AuthResponse response = new AuthResponse();
        response.setToken(jwtToken);
        response.setUsername(user.getUsername());
        return response;
    }

    public AuthResponse login(LoginDto loginDto) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginDto.getUsername(),
                loginDto.getPassword()
            )
        );
        User user = userRepository.findByUsername(loginDto.getUsername())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String jwtToken = jwtService.generateToken(new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), Collections.emptyList()));

        AuthResponse response = new AuthResponse();
        response.setToken(jwtToken);
        response.setUsername(user.getUsername());
        return response;
    }
}
