package com.cts.entity;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "emergency_contact", length = 15)
    private String emergencyContact;

    @Column(name = "address_line", length = 255)
    private String addressLine;

    @Column(name = "postal_code", length = 10)
    private String postalCode;
}