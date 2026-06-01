package com.cts.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "registrar")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Registrar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long registrarId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "emergency_contact", length = 15)
    private String emergencyContact;

    @Column(name = "address_line", length = 255)
    private String addressLine;

    @Column(name = "postal_code", length = 10)
    private String postalCode;
}