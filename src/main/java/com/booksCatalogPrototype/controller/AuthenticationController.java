package com.booksCatalogPrototype.controller;

import javax.validation.Valid;

import com.booksCatalogPrototype.dto.ApiResponse;
import com.booksCatalogPrototype.dto.JwtAuthenticationResponse;
import com.booksCatalogPrototype.dto.LoginRequest;
import com.booksCatalogPrototype.dto.SignUpRequest;
import com.booksCatalogPrototype.repository.UserRepository;
import com.booksCatalogPrototype.service.AuthenticationServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booksCatalogPrototype.dto.AdminDTO;
import com.booksCatalogPrototype.exception.custom.AuthenticationException;

@RestController
@RequestMapping(value = "/api/auth")
public class AuthenticationController {

    private final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private UserRepository userRepository;
    private AuthenticationServiceImpl authenticationService;

    @Autowired
    public AuthenticationController(UserRepository userRepository, AuthenticationServiceImpl authenticationService) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("In login method.");
	    JwtAuthenticationResponse response = authenticationService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    // TODO: 1/20/2021
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody SignUpRequest signUpRequest) {
        logger.info("In register method.");
        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
           throw new AuthenticationException("User with this username already exists");
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
	        throw new AuthenticationException("User with this email already exists");
        }
	    ApiResponse response = authenticationService.register(signUpRequest);

	    return ResponseEntity.ok(response);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/add_admin")
    public ResponseEntity<ApiResponse> addAdmin(@Valid @RequestBody AdminDTO adminDTO) {
        logger.info("In adding new admin method.");
        if(userRepository.existsByUsername(adminDTO.getUsername())) {
	        throw new AuthenticationException("Admin with this username already exists");
        }

        if(userRepository.existsByEmail(adminDTO.getEmail())) {
	        throw new AuthenticationException("Admin with this email already exists");
        }
	    ApiResponse response = authenticationService.addAdmin(adminDTO);
	    return ResponseEntity.ok(response);
    }


}
