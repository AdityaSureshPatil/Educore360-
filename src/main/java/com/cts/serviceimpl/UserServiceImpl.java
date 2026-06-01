package com.cts.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cts.dto.*;
import com.cts.entity.*;
import com.cts.enumerate.Role;
import com.cts.exception.*;
import com.cts.repository.*;
import com.cts.service.UserService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final InstructorRepository instructorRepository;
    private final StudentRepository studentRepository;
    private final RegistrarRepository registrarRepository;
    private final ExamCoordinatorRepository examCoordinatorRepository;
    private final FinanceOfficerRepository financeOfficerRepository;

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    @Override
    @Transactional
    public RegistrationOutputDTO addUser(RegistrationInputDTO registrationInputDTO) {

        if (!isValidEmail(registrationInputDTO.getEmail())) {
            throw new InvalidEmailException("Invalid email format");
        }
        if (userRepository.existsByEmail(registrationInputDTO.getEmail())) {
            throw new InvalidEmailException("Email is already registered!");
        }

        Role verifiedRole;
        try {
            verifiedRole = Role.valueOf(
                    registrationInputDTO.getRole().trim().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new InvalidEmailException(
                    "Invalid Role. Must be one of: STUDENT, INSTRUCTOR, " +
                    "REGISTRAR, EXAM_COORDINATOR, FINANCE_OFFICER");
        }

        User user = User.builder()
                .email(registrationInputDTO.getEmail().trim())
                .name(registrationInputDTO.getName())
                .password(passwordEncoder.encode(registrationInputDTO.getPassword()))
                .role(verifiedRole)
                .phone(registrationInputDTO.getPhone())
                .build();

        User savedUser = userRepository.save(user);

        switch (verifiedRole) {
            case INSTRUCTOR -> instructorRepository.save(
                    Instructor.builder()
                            .user(savedUser)
                            .status("ACTIVE")
                            .build());
            case STUDENT -> studentRepository.save(
                    Student.builder()
                            .user(savedUser)
                            .enrollmentNumber(
                                    generateEnrollmentNumber(savedUser.getUserId()))
                            .status("ACTIVE")
                            .build());
            case REGISTRAR -> registrarRepository.save(
                    Registrar.builder()
                            .user(savedUser)
                            .build());
            case EXAM_COORDINATOR -> examCoordinatorRepository.save(
                    ExamCoordinator.builder()
                            .user(savedUser)
                            .build());
            case FINANCE_OFFICER -> financeOfficerRepository.save(
                    FinanceOfficer.builder()
                            .user(savedUser)
                            .build());
        }

        return RegistrationOutputDTO.builder()
                .userId(savedUser.getUserId())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .role(savedUser.getRole())
                .phone(savedUser.getPhone())
                .status(savedUser.getStatus())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new NoDetailsAvailableException("No details available");
        }
        return users.stream()
                .map(this::mapToUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponseDTO getUserByEmail(String email) {
        if (!isValidEmail(email)) {
            throw new InvalidEmailException("Invalid email format");
        }
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User not found with email: " + email);
        }
        return mapToUserResponseDTO(user);
    }

    @Override
    public LoginResponseDTO userLogin(LoginDTO loginDTO) {

        // 1. Validate email format
        if (!isValidEmail(loginDTO.getEmail())) {
            throw new InvalidEmailException("Invalid email format");
        }

        // 2. Fetch user by email
        User user = userRepository.findByEmail(loginDTO.getEmail());
        if (user == null) {
            throw new UserNotFoundException("Invalid credentials");
        }

        // 3. Check password
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new UserNotFoundException("Invalid credentials");
        }

        // 4. Check account is active
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            throw new UserNotFoundException("User account is not active");
        }

        // 5. Auto-update lastLoginAt
        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        // 6. Return response — role fetched from DB, not from input
        return LoginResponseDTO.builder()
                .email(user.getEmail())
                .userName(user.getName())
                .role(user.getRole().name())
                .phoneNumber(user.getPhone())
                .build();
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private UserResponseDTO mapToUserResponseDTO(User user) {
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .phone(user.getPhone())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }

    private String generateEnrollmentNumber(Long userId) {
        return "ENR" + String.format("%06d", userId);
    }
}
