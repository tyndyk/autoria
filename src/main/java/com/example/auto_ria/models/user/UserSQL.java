package com.example.auto_ria.models.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.auto_ria.enums.ERole;
import com.example.auto_ria.enums.ESubscriptionStatus;
import com.example.auto_ria.models.car.CarSQL;
import com.example.auto_ria.models.premium.PremiumPlan;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = { "cars" })
public class UserSQL implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    private String avatar;
    private String password;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<ERole> roles = new ArrayList<>();

    private String city;
    private String country;
    private String number;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "premium_plan_id")
    private PremiumPlan premiumPlan;

    private boolean isActivated;

    @JsonBackReference
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "seller_cars", joinColumns = @JoinColumn(name = "seller_id"), inverseJoinColumns = @JoinColumn(name = "car_id"))
    private List<CarSQL> cars = new ArrayList<>();

    @Column(updatable = false)
    @CreationTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    @Builder
    public UserSQL(String name, String lastName, String email, String avatar, String password, List<ERole> roles,
            String city, String country, String number, boolean isActivated) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.avatar = avatar;
        this.password = password;
        this.roles = roles;
        this.city = city;
        this.country = country;
        this.number = number;
        this.isActivated = isActivated;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActivated;
    }

    public boolean isPremium() {
        return this.premiumPlan != null && this.premiumPlan.getStatus().equals(ESubscriptionStatus.ACTIVE);
    }

    public String getFullName() {
        return this.getName() + this.getLastName();
    }

    public ERole getFirstPriorityRole() {

        ERole[] roles = { ERole.USER, ERole.ADMIN, ERole.MANAGER };

        for (ERole role : roles) {
            if (role == ERole.MANAGER) {
                return ERole.MANAGER;
            } else if (role == ERole.ADMIN) {
                return ERole.ADMIN;
            } else if (role == ERole.USER) {
                return ERole.USER;
            }
        }
        throw new IllegalArgumentException("No valid roles found in the array.");
    }

}
