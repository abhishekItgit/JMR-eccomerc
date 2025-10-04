package com.ecommerce.auth_service.service.impl;
import com.ecommerce.auth_service.dto.AuthResponse;
import com.ecommerce.auth_service.dto.LoginRequest;
import com.ecommerce.auth_service.dto.SignupRequest;
import com.ecommerce.auth_service.entity.User;
import com.ecommerce.auth_service.repository.UserRepository;
import com.ecommerce.auth_service.security.JwtUtil;
import com.ecommerce.auth_service.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.Set;


@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;



    public AuthServiceImpl(UserRepository userRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponse signup(SignupRequest rq) {
        if (userRepo.existsByEmail(rq.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email exists");
        }
        User u = new User();
        u.setEmail(rq.email());
        u.setPassword(passwordEncoder.encode(rq.password()));
        u.setRoles(Set.of("ROLE_USER"));
        userRepo.save(u);
        return new AuthResponse(null, "User created successfully");
    }

    @Override
    public AuthResponse login(LoginRequest rq) {
        User u = userRepo.findByEmail(rq.email())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(rq.password(), u.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtUtil.generateToken(u.getEmail(), u.getRoles());
        return new AuthResponse(token, "Login successful");
    }
}