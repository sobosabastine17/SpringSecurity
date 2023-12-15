package com.example.SpringSecurity.Auth;

import com.example.SpringSecurity.Config.JwtService;
import com.example.SpringSecurity.Repo.UserRepository;
import com.example.SpringSecurity.User.Role;
import com.example.SpringSecurity.User.User;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@Builder
@RequiredArgsConstructor
public class AuthenticationServiceImp implements AuthenticationService{
    private final UserRepository repo;
    @Autowired
    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Constructor to inject the PasswordEncoder bean
//    public AuthenticationServiceImp(PasswordEncoder passwordEncoder) {
//        this.passwordEncoder = passwordEncoder;
//        this.repo=
//    }
    @Override
    public AuthenticationResponse register(RegistrationRequest request) {
        var user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        repo.save(user);
        var token = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(token)
                .build();
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));
        var user = repo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return AuthenticationResponse.builder()
                .token(jwtService.generateToken(user))
                .build();
    }
}
