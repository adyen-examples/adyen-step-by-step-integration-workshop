package com.adyen.workshop.controllers;

import com.adyen.model.RequestOptions;
import com.adyen.model.checkout.*;
import com.adyen.service.checkout.ModificationsApi;
import com.adyen.workshop.configurations.ApplicationConfiguration;
import com.adyen.service.checkout.PaymentsApi;
import com.adyen.service.exception.ApiException;
import com.adyen.workshop.service.SessionManager;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.UUID;

/**
 * REST controller for using the Adyen payments API.
 */
@RestController
public class ApiController {
    private final Logger log = LoggerFactory.getLogger(ApiController.class);

    private final ApplicationConfiguration applicationConfiguration;
    private final PaymentsApi paymentsApi;
    private final ModificationsApi modificationsApi;

    public ApiController(ApplicationConfiguration applicationConfiguration, PaymentsApi paymentsApi, ModificationsApi modificationsApi) {
        this.applicationConfiguration = applicationConfiguration;
        this.paymentsApi = paymentsApi;
        this.modificationsApi = modificationsApi;
    }


    @PostMapping("/api/paymentMethods")
    public ResponseEntity<PaymentMethodsResponse> paymentMethods() throws IOException, ApiException {

        return ResponseEntity.ok()
                .body(null);
    }

    @PostMapping("/api/payments")
    public ResponseEntity<PaymentResponse> payments(@RequestHeader String host, @RequestBody PaymentRequest body, HttpServletRequest request) throws IOException, ApiException {

        return ResponseEntity.ok()
                .body(null);
    }

    @PostMapping("/api/payments/details")
    public ResponseEntity<PaymentDetailsResponse> paymentsDetails(@RequestBody PaymentDetailsRequest detailsRequest) throws IOException, ApiException {
        // submit payment details

        return ResponseEntity.ok().body(null);
    }

    // Handle redirect during payment.
    @GetMapping("/api/handleShopperRedirect")
    public RedirectView redirect(@RequestParam(required = false) String payload, @RequestParam(required = false) String redirectResult) throws IOException, ApiException {
        var paymentDetailsRequest = new PaymentDetailsRequest();

        PaymentCompletionDetails paymentCompletionDetails = new PaymentCompletionDetails();

        // Handle redirect result or payload
        if (redirectResult != null && !redirectResult.isEmpty()) {
            // For redirect, you are redirected to an Adyen domain to complete the 3DS2 challenge
            // After completing the 3DS2 challenge, you get the redirect result from Adyen in the returnUrl
            // We then pass on the redirectResult
            paymentCompletionDetails.redirectResult(redirectResult);
        } else if (payload != null && !payload.isEmpty()) {
            paymentCompletionDetails.payload(payload);
        }

        paymentDetailsRequest.setDetails(paymentCompletionDetails);

        var paymentsDetailsResponse = paymentsApi.paymentsDetails(paymentDetailsRequest);
        log.info("PaymentsDetailsResponse {}", paymentsDetailsResponse);

        // Handle response
        return getRedirectView(paymentsDetailsResponse);
    }

    private RedirectView getRedirectView(final PaymentDetailsResponse paymentsDetailsResponse) {
        // Step 7
        var redirectURL = "/result/";
        switch (paymentsDetailsResponse.getResultCode()) {
            case AUTHORISED:
                redirectURL += "success";
                break;
            case PENDING:
            case RECEIVED:
                redirectURL += "pending";
                break;
            case REFUSED:
                redirectURL += "failed";
                break;
            default:
                redirectURL += "error";
                break;
        }
        return new RedirectView(redirectURL + "?reason=" + paymentsDetailsResponse.getResultCode());
    }

    @PostMapping("/api/refund")
    public ResponseEntity<PaymentRefundResponse> refund() throws Exception {

        return ResponseEntity.ok().body(null);
    }

}