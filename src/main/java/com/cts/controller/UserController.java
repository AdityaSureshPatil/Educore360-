package com.cts.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cts.dto.LoginDTO;
import com.cts.dto.LoginResponseDTO;
import com.cts.dto.RegistrationInputDTO;
import com.cts.dto.RegistrationOutputDTO;
import com.cts.dto.UserResponseDTO;
import com.cts.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // POST /user/register
    @PostMapping("/register")
    public ResponseEntity<RegistrationOutputDTO> addUser(@Valid @RequestBody RegistrationInputDTO registerRequestDTO){
        RegistrationOutputDTO savedUser = userService.addUser(registerRequestDTO);
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }

    // GET /user/all
    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDTO>> getAllUser() {
        List<UserResponseDTO> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    // GET /user/{email}
    @GetMapping("/{email}")
    public ResponseEntity<UserResponseDTO> getUserByEmail(@PathVariable String email) {
        UserResponseDTO user = userService.getUserByEmail(email);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    // POST /user/login
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> userLogin(
            @Valid @RequestBody LoginDTO loginDTO) {
        LoginResponseDTO response = userService.userLogin(loginDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}