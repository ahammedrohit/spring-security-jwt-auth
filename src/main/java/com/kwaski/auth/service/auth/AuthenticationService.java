package com.kwaski.auth.service.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kwaski.auth.entity.TokenEntity;
import com.kwaski.auth.entity.UserEntity;
import com.kwaski.auth.entity.auth.AuthenticationRequest;
import com.kwaski.auth.entity.auth.AuthenticationResponse;
import com.kwaski.auth.entity.auth.RegisterRequest;
import com.kwaski.auth.enums.Role;
import com.kwaski.auth.repository.postgres.TokenRepository;
import com.kwaski.auth.repository.postgres.UserRepository;
import com.kwaski.auth.service.TokenService;
import com.kwaski.auth.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    private final TokenService tokenService;


    public AuthenticationResponse register(RegisterRequest request) {
        UserEntity user = new UserEntity();
        user.setUser_name(request.getUser_name());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        userService.createUser(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        tokenService.saveToken(TokenEntity.builder()
                .user_name(user.getUser_name())
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .name(user.getUser_name())
                .role(String.valueOf(user.getRole()))
                .build();
    }

    public AuthenticationResponse authenticate(@NonNull AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUser_name(), request.getPassword(), null));
        var user = userService.getUserByUsername(request.getUser_name());
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .refreshToken(refreshToken)
                .name(user.getUser_name())
                .role(String.valueOf(user.getRole()))
                .build();
    }

    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;

        final String user_name;



        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        refreshToken = authHeader.substring(7);
        user_name = jwtService.extractUsername(refreshToken);



        if(user_name != null) {
            UserEntity userEntity;
            try {
                userEntity = userRepository.getUserByUsername(user_name);
            } catch (Exception e) {
                throw new RuntimeException("User not found");
            }
            if (jwtService.isTokenValid(refreshToken, userEntity)) {
                var accessToken = jwtService.generateToken(userEntity);
                tokenService.saveToken(TokenEntity.builder()
                        .user_name(userEntity.getUser_name())
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build());
                var authResponse = AuthenticationResponse.builder()
                        .token(accessToken)
                        .refreshToken(refreshToken)
                        .name(userEntity.getUser_name())
                        .role(String.valueOf(userEntity.getRole()))
                        .build();
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }
}
