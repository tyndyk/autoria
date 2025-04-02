package com.example.auto_ria.models.premium;

import java.time.LocalDate;

import com.example.auto_ria.enums.ESubscriptionStatus;
import com.example.auto_ria.models.user.UserSQL;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PremiumPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String customerId;

    @Builder.Default
    private String subId = null;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private UserSQL user;

    private LocalDate startDate;
    private LocalDate endDate;

    private ESubscriptionStatus status;
}

