package com.softserve.booksCatalogPrototype.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.softserve.booksCatalogPrototype.dto.AdminDTO;
import com.softserve.booksCatalogPrototype.dto.ApiResponse;
import com.softserve.booksCatalogPrototype.dto.JwtAuthenticationResponse;
import com.softserve.booksCatalogPrototype.dto.LoginRequest;
import com.softserve.booksCatalogPrototype.dto.SignUpRequest;
import com.softserve.booksCatalogPrototype.exception.custom.AuthenticationException;
import com.softserve.booksCatalogPrototype.repository.UserRepository;
import com.softserve.booksCatalogPrototype.service.impl.AuthenticationService;

@RestController
@RequestMapping(value = "/api/auth")
public class AuthenticationController {

    private UserRepository userRepository;

    private AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(UserRepository userRepository, AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
	    JwtAuthenticationResponse response = authenticationService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody SignUpRequest signUpRequest) {
        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
           throw new AuthenticationException("User with CHANGES this username already exists");
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
	        throw new AuthenticationException("User with this email already exists");
        }
	    ApiResponse response = authenticationService.register(signUpRequest);

	    return ResponseEntity.ok(response);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/add_admin")
    public ResponseEntity<ApiResponse> addAdmin(@Valid @RequestBody AdminDTO adminDTO) throws Exception{
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
