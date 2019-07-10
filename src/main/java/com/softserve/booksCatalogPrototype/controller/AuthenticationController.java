package com.softserve.booksCatalogPrototype.controller;

import com.softserve.booksCatalogPrototype.dto.*;
import com.softserve.booksCatalogPrototype.model.Role;
import com.softserve.booksCatalogPrototype.model.RoleName;
import com.softserve.booksCatalogPrototype.model.User;
import com.softserve.booksCatalogPrototype.repository.RoleRepository;
import com.softserve.booksCatalogPrototype.repository.UserRepository;
import com.softserve.booksCatalogPrototype.security.jwt.JwtTokenProvider;
import com.softserve.booksCatalogPrototype.util.DTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping(value = "/api/auth")
public class AuthenticationController {

    private AuthenticationManager authenticationManager;

    private UserRepository userRepository;

    private RoleRepository roleRepository;

    private PasswordEncoder passwordEncoder;

    private JwtTokenProvider tokenProvider;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> login(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody SignUpRequest signUpRequest) throws Exception {
        if(userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "Username is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "Email address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }

        User user = DTOConverter.convertSignUpRequestToUser(signUpRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (!roleRepository.findByName(RoleName.ROLE_USER).isPresent()){
            Role userRole = new Role(RoleName.ROLE_USER);
            Role saved = roleRepository.save(userRole);
            user.getRoles().add(saved);
            userRepository.save(user);
        } else {
            Role saved = roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new Exception("Role was not found"));
            user.getRoles().add(saved);
            userRepository.save(user);
        }

        return ResponseEntity.ok(new ApiResponse(true, "User registered successfully"));
    }

    @Secured("ROLE_ADMIN")
    @PostMapping("/add_admin")
    public ResponseEntity<ApiResponse> addAdmin(@Valid @RequestBody AdminDTO adminDTO) throws Exception{
        if(userRepository.existsByUsername(adminDTO.getUsername())) {
            return new ResponseEntity(new ApiResponse(false, "Admin name is already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(adminDTO.getEmail())) {
            return new ResponseEntity(new ApiResponse(false, "Email address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }
        User admin = DTOConverter.convertAdminDTOToUser(adminDTO);
        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        Role role = roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow(() -> new Exception("Role was not found"));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        admin.setRoles(roles);
        userRepository.save(admin);
        return ResponseEntity.ok(new ApiResponse(true, "Admin registered successfully"));
    }


}
