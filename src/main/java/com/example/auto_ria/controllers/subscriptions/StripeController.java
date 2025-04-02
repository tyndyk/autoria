package com.example.auto_ria.controllers.subscriptions;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auto_ria.dto.responces.ResponceObj;
import com.example.auto_ria.dto.responces.builder.ResponseBuilder;
import com.example.auto_ria.services.subscriptions.StripeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
@Tag(name = "Payment Management", description = "Stripe payment and subscription management")
public class StripeController {

    private final StripeService stripeService;

    @Operation(summary = "Create a new checkout session", description = "Creates a checkout session for a user to start a new subscription.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Checkout session created successfully"),
            @ApiResponse(responseCode = "403", description = "User already has an active subscription"),
            @ApiResponse(responseCode = "500", description = "Stripe session creation failed")
    })
    @PostMapping("/create-checkout-session")
    public ResponseEntity<ResponceObj<Map<String, String>>> createCheckoutSession() {
        return ResponseBuilder.buildResponse(stripeService.createSubscription());
    }

    @Operation(summary = "Cancel a subscription", description = "Cancels an active subscription for the given user ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Subscription canceled successfully"),
            @ApiResponse(responseCode = "403", description = "User is not authorized to cancel this subscription"),
            @ApiResponse(responseCode = "404", description = "Subscription not found"),
            @ApiResponse(responseCode = "500", description = "Stripe subscription cancellation failed")
    })
    @PostMapping("/cancel-subscription/{id}")
    public ResponseEntity<ResponceObj<String>> cancelSubscription(
            @PathVariable("id") @Parameter(description = "User ID whose subscription needs to be canceled") int id) {
        return ResponseBuilder.buildResponse(stripeService.cancelSubscription(id));
    }

    @Operation(summary = "Payment success callback", description = "Callback triggered when payment is successful.")
    @ApiResponse(responseCode = "200", description = "Payment was successful")
    @GetMapping("/success")
    public ResponseEntity<ResponceObj<String>> paymentSuccessful() {
        return ResponseBuilder.buildResponse("The payment was successfully made");
    }

    @Operation(summary = "Payment failure callback", description = "Callback triggered when payment fails.")
    @ApiResponse(responseCode = "200", description = "Payment failed")
    @GetMapping("/fail")
    public ResponseEntity<ResponceObj<String>> paymentFailed() {
        return ResponseBuilder.buildResponse("The payment failed");
    }
}