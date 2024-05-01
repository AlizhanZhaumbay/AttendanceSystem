package com.example.attendance_system.auth;

import com.example.attendance_system.exception.TokenExpiredException;
import com.example.attendance_system.exception.UserAlreadyExists;
import com.example.attendance_system.exception.UserNotFoundException;
import com.example.attendance_system.model.Person;
import com.example.attendance_system.model.Token;
import com.example.attendance_system.model.TokenType;
import com.example.attendance_system.model.User;
import com.example.attendance_system.repo.PersonRepository;
import com.example.attendance_system.repo.TokenRepository;
import com.example.attendance_system.repo.UserRepository;
import com.example.attendance_system.service.JwtService;
import com.example.attendance_system.util.ExceptionMessage;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PersonRepository personRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var userBuilder = User.builder()
                .login(request.login())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role());
        if(request.personId() != null){
            userBuilder.person(personRepository.findById(request.personId())
                    .orElse(null));
        }
        User user = userBuilder.build();
        if(userRepository.existsByLogin(request.login())){
            throw new UserAlreadyExists(ExceptionMessage.userAlreadyExistsWithLogin(request.login()));
        }
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);

        saveUserToken(savedUser, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.login(),
                        request.password()
                )
        );
        var user = userRepository.findByLogin(request.login())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .login(user.getLogin())
                .role(user.getRole())
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public AuthenticationResponse refreshToken(
            HttpServletRequest request
    ){
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userLogin;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UserNotFoundException("No token found.");
        }
        refreshToken = authHeader.substring(7);
        userLogin = jwtService.extractUsername(refreshToken);
        if (userLogin != null) {
            var user = userRepository.findByLogin(userLogin)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user)) {
                var accessToken = jwtService.generateToken(user);
                revokeAllUserTokens(user);
                saveUserToken(user, accessToken);
                return AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .login(user.getLogin())
                        .role(user.getRole())
                        .build();
            }
            throw new TokenExpiredException("Your refresh token has expired.");
        }

        throw new UserNotFoundException("Invalid login provided.");
    }
}