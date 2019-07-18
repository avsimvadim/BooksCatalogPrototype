package com.softserve.booksCatalogPrototype.service;

import com.softserve.booksCatalogPrototype.dto.AdminDTO;
import com.softserve.booksCatalogPrototype.dto.ApiResponse;
import com.softserve.booksCatalogPrototype.dto.JwtAuthenticationResponse;
import com.softserve.booksCatalogPrototype.dto.LoginRequest;
import com.softserve.booksCatalogPrototype.dto.SignUpRequest;

public interface AuthenticationServiceInterface {

	ApiResponse register(SignUpRequest signUpRequest);

	JwtAuthenticationResponse login(LoginRequest loginRequest);

	ApiResponse addAdmin(AdminDTO adminDTO);
}
