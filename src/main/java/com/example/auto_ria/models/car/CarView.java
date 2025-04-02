package com.example.auto_ria.models.car;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "car_views")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarView {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long carId;

    @Column(nullable = true)
    private Long userId;

    @Column(nullable = false)
    private Instant viewedAt;

    @Column(nullable = true)
    private String ipAddress;

    @Column(nullable = true)
    private String userAgent;
}
