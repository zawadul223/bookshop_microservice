package com.bjit.authentication_service.service.Implementation;

import com.bjit.authentication_service.entity.Role;
import com.bjit.authentication_service.entity.UserEntity;
import com.bjit.authentication_service.model.AuthenticationRequest;
import com.bjit.authentication_service.model.AuthenticationResponse;
import com.bjit.authentication_service.model.UserRequestModel;
import com.bjit.authentication_service.repository.UserRepository;
import com.bjit.authentication_service.service.UserService;
import com.bjit.authentication_service.utils.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    @Override
    public ResponseEntity<Object> register(UserRequestModel userRequest) {
        UserEntity userEntity = UserEntity.builder()
                .email(userRequest.getEmail())
                .userName(userRequest.getUserName())
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .role(Objects.equals(userRequest.getRole(), "ADMIN") ? Role.ADMIN:Role.USER)
                .build();
        userRepository.save(userEntity);
//        AuthenticationResponse authRes = AuthenticationResponse.builder()
//                .token(jwtService.generateToken(userEntity))
//                .build();
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getEmail(),
                        authenticationRequest.getPassword()
                )
        );
        var user = userRepository.findByEmail(authenticationRequest.getEmail());
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();

    }

    @Override
    public UserEntity findByEmail(String email){
        return userRepository.findByEmail(email);
    }

}
