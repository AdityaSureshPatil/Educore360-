package com.cts.service;

import com.cts.dto.RegistrarInputDTO;
import com.cts.dto.RegistrarOutputDTO;

public interface RegistrarProfileService {

    // Update registrar profile — emergencyContact, addressLine, postalCode
    RegistrarOutputDTO updateRegistrarProfile(RegistrarInputDTO inputDTO);
}