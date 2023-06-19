package com.kwaski.auth.service.auth;

import com.kwaski.auth.entity.TokenEntity;
import com.kwaski.auth.entity.UserEntity;
import com.kwaski.auth.entity.auth.AuthenticationRequest;
import com.kwaski.auth.entity.auth.AuthenticationResponse;
import com.kwaski.auth.entity.auth.RegisterRequest;
import com.kwaski.auth.enums.Role;
import com.kwaski.auth.repository.postgres.TokenRepository;
import com.kwaski.auth.service.TokenService;
import com.kwaski.auth.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;


    public AuthenticationResponse register(RegisterRequest request) {
        UserEntity user = new UserEntity();
        user.setUser_name(request.getUser_name());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        userService.createUser(user);
        var jwtToken = jwtService.generateToken(user);
        tokenService.saveToken(TokenEntity.builder()
                .user_name(user.getUser_name())
                .accessToken(jwtToken)
                .build());
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .name(user.getUser_name())
                .role(String.valueOf(user.getRole()))
                .build();
    }

    public AuthenticationResponse authenticate(@NonNull AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUser_name(), request.getPassword(), null));
        var user = userService.getUserByUsername(request.getUser_name());
        var jwtToken = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .token(jwtToken)
                .name(user.getUser_name())
                .role(String.valueOf(user.getRole()))
                .build();
    }
}
