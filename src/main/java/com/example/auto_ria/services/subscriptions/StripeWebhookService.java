package com.example.auto_ria.services.subscriptions;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.auto_ria.configurations.properties.StripeConfigProperties;
import com.example.auto_ria.dto.responces.ResponceObj;
import com.example.auto_ria.dto.responces.builder.ResponseBuilder;
import com.example.auto_ria.mail.MailerService;
import com.example.auto_ria.models.premium.PremiumPlan;
import com.example.auto_ria.models.user.UserSQL;
import com.example.auto_ria.services.user.UsersServiceSQL;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.Invoice;
import com.stripe.model.Subscription;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeWebhookService {

    private final MailerService mailer;
    private final SubscriptionService subscriptionService;
    private final UsersServiceSQL usersService;
    private final StripeConfigProperties stripeConfigProperties;

    public ResponseEntity<ResponceObj<String>> handleStripeEvent(String payload, String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, stripeConfigProperties.getWebhookKey());
            log.info("Received Stripe event: {}", event.getType());

            switch (event.getType()) {
                case "checkout.session.completed":
                    handleCheckoutSessionCompleted(deserializeEvent(event, Session.class));
                    break;
                case "checkout.session.async_payment_failed":
                    handleCheckoutSessionFailed(deserializeEvent(event, Session.class));
                    break;
                case "invoice.paid":
                    handleInvoicePaymentSucceeded(deserializeEvent(event, Invoice.class));
                    break;
                case "invoice.payment_failed":
                    handleInvoicePaymentFailed(deserializeEvent(event, Invoice.class));
                    break;
                case "customer.subscription.deleted":
                    handleSubscriptionDeleted(deserializeEvent(event, Subscription.class));
                    break;
                default:
                    log.warn("Unhandled Stripe event type: {}", event.getType());
            }

            return ResponseBuilder.buildResponse("Event processed successfully");

        } catch (SignatureVerificationException e) {
            log.error("Invalid Stripe signature: {}", e.getMessage());
            return ResponseBuilder.buildResponse(HttpStatus.UNAUTHORIZED, "Webhook error", "Invalid signature");

        } catch (IllegalArgumentException e) {
            log.error("Failed to deserialize Stripe event: {}", e.getMessage());
            return ResponseBuilder.buildResponse(HttpStatus.BAD_REQUEST, "Webhook error", "Invalid event data");

        } catch (Exception e) {
            log.error("Unexpected error while processing Stripe event: {}", e.getMessage(), e);
            return ResponseBuilder.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Webhook error", "Internal error");
        }
    }

    // Safe deserialization of Stripe events
    private <T> T deserializeEvent(Event event, Class<T> clazz) {
        return event.getDataObjectDeserializer().getObject()
                .map(clazz::cast)
                .orElseThrow(() -> new IllegalArgumentException("Failed to deserialize event: " + event.getType()));
    }

    private void handleCheckoutSessionCompleted(Session session) {
        log.info("Processing successful checkout session for customer: {}", session.getCustomer());

        PremiumPlan plan = subscriptionService.activateSubscription(session);
        UserSQL user = plan.getUser();
        user.setPremiumPlan(plan);
        usersService.save(user);

        mailer.handleEmail(() -> mailer.sendPremiumBoughtEmail(user.getEmail(), user.getFullName()));
    }

    private void handleCheckoutSessionFailed(Session session) {
        log.warn("Checkout session failed for customer: {}", session.getCustomer());

        PremiumPlan plan = subscriptionService.setPremiumPaymentFailed(session);
        mailer.handleEmail(() -> mailer.sendPremiumStartFailedEmail(plan.getUser().getEmail(),
                plan.getUser().getFullName(), LocalDate.now()));
    }

    private void handleInvoicePaymentSucceeded(Invoice invoice) {
        log.info("Invoice payment successful for customer: {}", invoice.getCustomer());

        PremiumPlan plan = subscriptionService.prolongSubscription(invoice);
        mailer.handleEmail(() -> mailer.sendPremiumContinueEmail(plan.getUser().getEmail(),
                plan.getUser().getFullName(), plan.getEndDate().plusMonths(1), LocalDateTime.now()));
    }

    private void handleInvoicePaymentFailed(Invoice invoice) {
        log.warn("Invoice payment failed for customer: {}", invoice.getCustomer());

        PremiumPlan plan = subscriptionService.handleProlongFailed(invoice);
        mailer.handleEmail(() -> mailer.sendPremiumContinueFailedEmail(plan.getUser().getEmail(),
                plan.getUser().getFullName(), LocalDateTime.now()));
    }

    private void handleSubscriptionDeleted(Subscription subscription) {
        log.info("Subscription canceled for customer: {}", subscription.getCustomer());

        PremiumPlan plan = subscriptionService.deleteSubscription(subscription.getCustomer());
        mailer.handleEmail(() -> mailer.sendPremiumCanceledEmail(plan.getUser().getEmail(),
                plan.getUser().getFullName(), LocalDateTime.now()));
    }
}
