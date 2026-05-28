package com.cts.service;

import java.util.List;
import com.cts.dto.LoginDTO;
import com.cts.dto.LoginResponseDTO;
import com.cts.dto.RegistrationInputDTO;
import com.cts.dto.RegistrationOutputDTO;
import com.cts.dto.UserResponseDTO;

public interface UserService {

    RegistrationOutputDTO addUser(RegistrationInputDTO registrationInputDTO);

    List<UserResponseDTO> getAllUsers();

    UserResponseDTO getUserByEmail(String email);

    LoginResponseDTO userLogin(LoginDTO loginDTO);
}