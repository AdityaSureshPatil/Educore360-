package com.cts.serviceimpl;

import org.springframework.stereotype.Service;
import com.cts.dto.RegistrarInputDTO;
import com.cts.dto.RegistrarOutputDTO;
import com.cts.entity.Registrar;
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

        // Find registrar profile auto-created during /user/register
        Registrar registrar = registrarRepository
                .findByUserUserId(inputDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Registrar profile not found for user id: " + inputDTO.getUserId()
                        + ". Make sure user is registered with role REGISTRAR."));

        // Update the 3 manual fields
        registrar.setEmergencyContact(inputDTO.getEmergencyContact());
        registrar.setAddressLine(inputDTO.getAddressLine());
        registrar.setPostalCode(inputDTO.getPostalCode());

        Registrar saved = registrarRepository.save(registrar);

        // Live count of courses assigned to this registrar
        int publishedCourseCount = courseRepository
                .countByInstructor_InstructorIdIsNotNull();

        return mapToOutputDTO(saved, publishedCourseCount);
    }

    private RegistrarOutputDTO mapToOutputDTO(Registrar registrar,
                                               int publishedCourseCount) {
        return RegistrarOutputDTO.builder()
                .registrarId(registrar.getRegistrarId())
                .emergencyContact(registrar.getEmergencyContact())
                .addressLine(registrar.getAddressLine())
                .postalCode(registrar.getPostalCode())
                .publishedCourseCount(publishedCourseCount)
                .userId(registrar.getUser().getUserId())
                .name(registrar.getUser().getName())
                .email(registrar.getUser().getEmail())
                .role(registrar.getUser().getRole().name())
                .status(registrar.getUser().getStatus())
                .build();
    }
}