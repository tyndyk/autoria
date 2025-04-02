package com.example.auto_ria.services.subscriptions;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.example.auto_ria.dao.premium.PremiumPlanDaoSQL;
import com.example.auto_ria.enums.ESubscriptionStatus;
import com.example.auto_ria.exceptions.user.UserNotFoundException;
import com.example.auto_ria.models.premium.PremiumPlan;
import com.example.auto_ria.models.user.UserSQL;
import com.stripe.model.Invoice;
import com.stripe.model.checkout.Session;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class SubscriptionService {

    private PremiumPlanDaoSQL premiumPlanDaoSQL;

    public void deletePremiumByUser(UserSQL user) {
        premiumPlanDaoSQL.deleteAllByUser(user);
    }

    public PremiumPlan createPremiumPlan(UserSQL user, String customerId) {
        PremiumPlan plan = PremiumPlan.builder()
                .status(ESubscriptionStatus.PENDING)
                .customerId(customerId)
                .endDate(LocalDate.now().plusMonths(1))
                .startDate(LocalDate.now())
                .customerId(customerId)
                .user(user)
                .build();

        return premiumPlanDaoSQL.save(plan);
    }

    public PremiumPlan getPremiumPlanInDB(String customerId) {
        return premiumPlanDaoSQL.findByCustomerId(customerId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public PremiumPlan activateSubscription(Session session) {
        PremiumPlan plan = getPremiumPlanInDB(session.getCustomer());

        plan.setStatus(ESubscriptionStatus.ACTIVE);
        plan.setEndDate(LocalDate.now());
        plan.setSubId(session.getSubscription());
        premiumPlanDaoSQL.save(plan);

        return plan;
    }

    public PremiumPlan setPremiumPaymentFailed(Session session) {
        PremiumPlan plan = getPremiumPlanInDB(session.getCustomer());

        plan.setStatus(ESubscriptionStatus.FAILED);
        return premiumPlanDaoSQL.save(plan);
    }

    public PremiumPlan prolongSubscription(Invoice invoice) {
        PremiumPlan plan = getPremiumPlanInDB(invoice.getCustomer());
        plan.setEndDate(LocalDate.now());
        return premiumPlanDaoSQL.save(plan);
    }

    public PremiumPlan handleProlongFailed(Invoice invoice) {
        PremiumPlan plan = getPremiumPlanInDB(invoice.getCustomer());

        plan.setStatus(ESubscriptionStatus.FAILED);
        return premiumPlanDaoSQL.save(plan);
    }

    public PremiumPlan deleteSubscription(String customerId) {
        PremiumPlan plan = getPremiumPlanInDB(customerId);

        plan.setStatus(ESubscriptionStatus.CANCELED);
        return premiumPlanDaoSQL.save(plan);
    }
}
