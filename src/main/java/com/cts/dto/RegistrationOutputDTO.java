package com.cts.dto;

import java.time.LocalDate;
import lombok.*;
import com.cts.enumerate.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegistrationOutputDTO {

    private Long userId;
    private String email;
    private String name;
    private Role role;
    private String phone;
    private String status;
    private LocalDate createdAt;
}