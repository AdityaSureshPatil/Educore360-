package com.cts.service;

import com.cts.dto.InstructorInputDTO;
import com.cts.dto.InstructorOutputDTO;

public interface InstructorService {

    // Update instructor profile details (auto-created on /user/register)
    InstructorOutputDTO updateInstructorProfile(InstructorInputDTO inputDTO);
}