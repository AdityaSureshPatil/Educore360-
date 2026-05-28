package com.cts.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "finance_officer")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FinanceOfficer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long financeOfficerId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}