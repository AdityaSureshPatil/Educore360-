package com.cts.serviceimpl;

import java.time.LocalDate;
import java.time.Period;
import org.springframework.stereotype.Service;
import com.cts.dto.InstructorInputDTO;
import com.cts.dto.InstructorOutputDTO;
import com.cts.entity.Instructor;
import com.cts.exception.BusinessException;
import com.cts.exception.InstructorNotFoundException;
import com.cts.repository.InstructorRepository;
import com.cts.service.InstructorService;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository instructorRepository;

    @Override
    public InstructorOutputDTO updateInstructorProfile(InstructorInputDTO inputDTO) {

        Instructor instructor = instructorRepository
                .findByUser_UserId(inputDTO.getUserId())
                .orElseThrow(() -> new InstructorNotFoundException(
                        "Instructor profile not found for user id: " + inputDTO.getUserId()
                        + ". Make sure user is registered with role INSTRUCTOR."));

        // Age validation — minimum 25 years
        if (inputDTO.getDateOfBirth() != null) {
            int age = Period.between(inputDTO.getDateOfBirth(), LocalDate.now()).getYears();
            if (age < 25) {
                throw new BusinessException(
                        "Instructor must be at least 25 years old, Please provide the correct age.");
            }
        }

        instructor.setSkill(inputDTO.getSkill());
        instructor.setExperience(inputDTO.getExperience());
        instructor.setDateOfBirth(inputDTO.getDateOfBirth());
        instructor.setEmergencyContact(inputDTO.getEmergencyContact());
        instructor.setAddressLine(inputDTO.getAddressLine());
        instructor.setPostalCode(inputDTO.getPostalCode());

        return mapToOutputDTO(instructorRepository.save(instructor));
    }

    private InstructorOutputDTO mapToOutputDTO(Instructor instructor) {
        return InstructorOutputDTO.builder()
                .instructorId(instructor.getInstructorId())
                .skill(instructor.getSkill())
                .experience(instructor.getExperience())
                .dateOfBirth(instructor.getDateOfBirth())
                .status(instructor.getStatus())
                .emergencyContact(instructor.getEmergencyContact())
                .addressLine(instructor.getAddressLine())
                .postalCode(instructor.getPostalCode())
                .userId(instructor.getUser().getUserId())
                .name(instructor.getUser().getName())
                .email(instructor.getUser().getEmail())
                .role(instructor.getUser().getRole().name())
                .build();
    }
}