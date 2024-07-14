package com.booksCatalogPrototype.service;

import com.booksCatalogPrototype.dto.AdminDTO;
import com.booksCatalogPrototype.dto.ApiResponse;
import com.booksCatalogPrototype.dto.JwtAuthenticationResponse;
import com.booksCatalogPrototype.dto.LoginRequest;
import com.booksCatalogPrototype.dto.SignUpRequest;

public interface AuthenticationService {

	ApiResponse register(SignUpRequest signUpRequest);

	JwtAuthenticationResponse login(LoginRequest loginRequest);

	ApiResponse addAdmin(AdminDTO adminDTO);
}
