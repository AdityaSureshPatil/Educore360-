package com.cts.serviceimpl;

import java.time.LocalDate;
import java.time.Period;
import org.springframework.stereotype.Service;
import com.cts.dto.RegistrarInputDTO;
import com.cts.dto.RegistrarOutputDTO;
import com.cts.entity.Registrar;
import com.cts.exception.BusinessException;
import com.cts.exception.ResourceNotFoundException;
import com.cts.repository.CourseRepository;
import com.cts.repository.RegistrarRepository;
import com.cts.service.RegistrarProfileService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RegistrarProfileServiceImpl implements RegistrarProfileService {

    private final RegistrarRepository registrarRepository;
    private final CourseRepository courseRepository;

    @Override
    public RegistrarOutputDTO updateRegistrarProfile(RegistrarInputDTO inputDTO) {

        Registrar registrar = registrarRepository
                .findByUserUserId(inputDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Registrar profile not found for user id: " + inputDTO.getUserId()
                        + ". Make sure user is registered with role REGISTRAR."));

        // Age validation — minimum 25 years
        if (inputDTO.getDateOfBirth() != null) {
            int age = Period.between(inputDTO.getDateOfBirth(), LocalDate.now()).getYears();
            if (age < 25) {
                throw new BusinessException(
                		"Registrar must be at least 25 years old, Please provide the correct age.");
            }
        }

        registrar.setDateOfBirth(inputDTO.getDateOfBirth());
        registrar.setEmergencyContact(inputDTO.getEmergencyContact());
        registrar.setAddressLine(inputDTO.getAddressLine());
        registrar.setPostalCode(inputDTO.getPostalCode());

        Registrar saved = registrarRepository.save(registrar);

        int publishedCourseCount = courseRepository
                .countByInstructor_InstructorIdIsNotNull();

        return RegistrarOutputDTO.builder()
                .registrarId(saved.getRegistrarId())
                .dateOfBirth(saved.getDateOfBirth())
                .emergencyContact(saved.getEmergencyContact())
                .addressLine(saved.getAddressLine())
                .postalCode(saved.getPostalCode())
                .publishedCourseCount(publishedCourseCount)
                .userId(saved.getUser().getUserId())
                .name(saved.getUser().getName())
                .email(saved.getUser().getEmail())
                .role(saved.getUser().getRole().name())
                .status(saved.getUser().getStatus())
                .build();
    }
}