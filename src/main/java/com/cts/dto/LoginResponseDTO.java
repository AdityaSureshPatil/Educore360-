package com.cts.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDTO {

    private String email;
    private String userName;
    private String role;
    private String phoneNumber;
}