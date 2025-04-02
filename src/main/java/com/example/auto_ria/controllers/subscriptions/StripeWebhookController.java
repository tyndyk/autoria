package com.example.auto_ria.controllers.subscriptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auto_ria.dto.responces.ResponceObj;
import com.example.auto_ria.services.subscriptions.StripeWebhookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/stripe-webhook")
@Tag(name = "Stripe Webhook", description = "Handles incoming Stripe events")
public class StripeWebhookController {

    private final StripeWebhookService stripeWebhookService;

    @Operation(
        summary = "Handle Stripe Webhook Event",
        description = "Receives and processes webhook events from Stripe, such as payment updates and subscription changes."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Event processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid payload or missing signature"),
        @ApiResponse(responseCode = "500", description = "Internal error processing the event")
    })
    @PostMapping("/receive")
    public ResponseEntity<ResponceObj<String>> handleStripeEvent(
        @RequestBody @Parameter(description = "Raw Stripe webhook payload") String payload,
        @RequestHeader("Stripe-Signature") @Parameter(description = "Stripe webhook signature header") String sigHeader
    ) {
        return stripeWebhookService.handleStripeEvent(payload, sigHeader);
    }
}
