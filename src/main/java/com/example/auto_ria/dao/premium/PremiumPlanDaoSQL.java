package com.example.auto_ria.dao.premium;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auto_ria.models.premium.PremiumPlan;
import com.example.auto_ria.models.user.UserSQL;

public interface PremiumPlanDaoSQL extends JpaRepository<PremiumPlan, Integer> {

    Optional<PremiumPlan> findByCustomerId(String customerId);

    void deleteAllByUser(UserSQL user);

}
