package com.cts.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.*;
import com.cts.enumerate.Role;

import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
 
    private String phone;

    private String status;

    private LocalDate createdAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
        if (this.status == null) {
            this.status = "ACTIVE";
        }
    }
}