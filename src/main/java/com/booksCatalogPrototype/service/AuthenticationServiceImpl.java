package com.booksCatalogPrototype.service;

import java.util.HashSet;
import java.util.Set;

import com.booksCatalogPrototype.dto.ApiResponse;
import com.booksCatalogPrototype.dto.JwtAuthenticationResponse;
import com.booksCatalogPrototype.dto.LoginRequest;
import com.booksCatalogPrototype.dto.SignUpRequest;
import com.booksCatalogPrototype.model.Role;
import com.booksCatalogPrototype.model.RoleName;
import com.booksCatalogPrototype.model.User;
import com.booksCatalogPrototype.repository.RoleRepository;
import com.booksCatalogPrototype.repository.UserRepository;
import com.booksCatalogPrototype.util.DTOConverter;
import com.booksCatalogPrototype.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.booksCatalogPrototype.dto.AdminDTO;
import com.booksCatalogPrototype.exception.custom.AuthenticationException;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	private AuthenticationManager authenticationManager;

	private UserRepository userRepository;

	private RoleRepository roleRepository;

	private PasswordEncoder passwordEncoder;

	private JwtTokenProvider tokenProvider;

	@Autowired
	public AuthenticationServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtTokenProvider tokenProvider) {
		this.authenticationManager = authenticationManager;
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.tokenProvider = tokenProvider;
	}


	@Override
	public ApiResponse register(SignUpRequest signUpRequest) {
		User user = DTOConverter.convertSignUpRequestToUser(signUpRequest);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		Role role = roleRepository.findByName(RoleName.ROLE_USER)
				.orElseThrow(() -> new AuthenticationException("Role was not found"));
		user.getRoles().add(role);
		userRepository.save(user);
		return new ApiResponse(true, "User registered successfully");
	}

	@Override
	public JwtAuthenticationResponse login(LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						loginRequest.getUsernameOrEmail(),
						loginRequest.getPassword()
				)
		);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = tokenProvider.generateToken(authentication);
		return new JwtAuthenticationResponse(jwt);
	}

	@Override
	public ApiResponse addAdmin(AdminDTO adminDTO) {
		User admin = DTOConverter.convertAdminDTOToUser(adminDTO);
		admin.setPassword(passwordEncoder.encode(admin.getPassword()));
		Role role = roleRepository.findByName(RoleName.ROLE_ADMIN)
				.orElseThrow(() -> new AuthenticationException("Role was not found"));
		Set<Role> roles = new HashSet<>();
		roles.add(role);
		admin.setRoles(roles);
		userRepository.save(admin);
		return new ApiResponse(true, "Admin registered successfully");
	}
}
