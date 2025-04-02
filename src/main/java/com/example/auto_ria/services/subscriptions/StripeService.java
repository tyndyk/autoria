package com.example.auto_ria.services.subscriptions;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.auto_ria.configurations.properties.StripeConfigProperties;
import com.example.auto_ria.enums.ESubscriptionStatus;
import com.example.auto_ria.exceptions.auth.PermissionDeniedException;
import com.example.auto_ria.exceptions.general.InternalServerException;
import com.example.auto_ria.models.user.UserSQL;
import com.example.auto_ria.services.auth.PermissionService;
import com.example.auto_ria.services.user.UsersServiceSQL;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StripeService {

    private final StripeConfigProperties stripeConfigProperties;
    private final PermissionService permissionService;
    private final UsersServiceSQL usersServiceMySQL;
    private final SubscriptionService subscriptionService;

    public Map<String, String> createSubscription() {
        UserSQL user = permissionService.getAuthenticatedUser();
        if (!isSubscriptionExpired(user)) {
            throw new PermissionDeniedException("Current subscription is still active");
        }

        Customer customer = resolveCustomer(user);
        SessionCreateParams params = buildSessionParams(customer.getId());
        Session session = createStripeSession(params);

        subscriptionService.createPremiumPlan(user, customer.getId());
        return Map.of("checkoutUrl", session.getUrl());
    }

    private Customer resolveCustomer(UserSQL user) {
        if (user.getPremiumPlan() == null || user.getPremiumPlan().getCustomerId() == null) {
            return createStripeCustomer(user);
        } else {
            if (!isSubscriptionExpired(user)) {
                throw new PermissionDeniedException("Current subscription is still active");
            }
            return retrieveStripeCustomer(user.getPremiumPlan().getCustomerId());
        }
    }

    public String cancelSubscription(int userId) {
        UserSQL user = usersServiceMySQL.getById(userId);
        permissionService.allowedToCancelSubscription(user);

        String subscriptionId = user.getPremiumPlan().getSubId();
        Subscription subscription = retrieveStripeSubscription(subscriptionId);
        cancelStripeSubscription(subscription);

        user.getPremiumPlan().setStatus(ESubscriptionStatus.CANCELED);
        usersServiceMySQL.save(user);
        return "Subscription canceled successfully";
    }

    private Customer retrieveStripeCustomer(String customerId) {
        try {
            return Customer.retrieve(customerId);
        } catch (StripeException e) {
            throw new InternalServerException("Failed to retrieve customer from Stripe");
        }
    }

    private Session createStripeSession(SessionCreateParams params) {
        try {
            return Session.create(params);
        } catch (StripeException e) {
            throw new InternalServerException("Failed to create Stripe checkout session");
        }
    }

    private SessionCreateParams buildSessionParams(String customerId) {
        return SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setSuccessUrl(stripeConfigProperties.getSuccessUrl())
                .setCancelUrl(stripeConfigProperties.getCancelUrl())
                .setCustomer(customerId)
                .setSavedPaymentMethodOptions(
                        SessionCreateParams.SavedPaymentMethodOptions.builder()
                                .setPaymentMethodSave(
                                        SessionCreateParams.SavedPaymentMethodOptions.PaymentMethodSave.ENABLED)
                                .build())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPrice(stripeConfigProperties.getPriceId())
                                .setQuantity(1L)
                                .build())
                .build();
    }

    private boolean isSubscriptionExpired(UserSQL user) {
        return user.getPremiumPlan() == null || user.getPremiumPlan().getEndDate().isBefore(LocalDate.now());
    }

    private Customer createStripeCustomer(UserSQL user) {
        try {
            return Customer.create(
                    CustomerCreateParams.builder()
                            .setName(user.getName() + " " + user.getLastName())
                            .setEmail(user.getEmail())
                            .build());
        } catch (StripeException e) {
            throw new InternalServerException("Failed to create Stripe customer");
        }
    }

    private void cancelStripeSubscription(Subscription subscription) {
        try {
            subscription.cancel();
        } catch (StripeException e) {
            throw new InternalServerException("Failed to cancel Stripe subscription");
        }
    }

    private Subscription retrieveStripeSubscription(String subscriptionId) {
        try {
            return Subscription.retrieve(subscriptionId);
        } catch (StripeException e) {
            throw new InternalServerException("Failed to retrieve subscription");
        }
    }
}
