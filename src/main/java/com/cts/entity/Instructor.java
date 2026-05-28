package com.cts.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "instructor")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instructor_id")
    private Long instructorId;

    @Column(name = "skill")
    private String skill;

    @Column(name = "experience")
    private Integer experience;

    @Column(name = "status")
    private String status;

    @Column(name = "emergency_contact", length = 15)
    private String emergencyContact;

    @Column(name = "address_line", length = 255)
    private String addressLine;

    @Column(name = "postal_code", length = 10)
    private String postalCode;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;
}