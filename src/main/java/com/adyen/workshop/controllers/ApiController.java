package com.adyen.workshop.controllers;

import com.adyen.model.RequestOptions;
import com.adyen.model.checkout.*;
import com.adyen.workshop.configurations.ApplicationConfiguration;
import com.adyen.service.checkout.PaymentsApi;
import com.adyen.service.exception.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for using the Adyen payments API.
 */
@RestController
public class ApiController {
    private final Logger log = LoggerFactory.getLogger(ApiController.class);

    private final ApplicationConfiguration applicationConfiguration;
    private final PaymentsApi paymentsApi;

    public ApiController(ApplicationConfiguration applicationConfiguration, PaymentsApi paymentsApi) {
        this.applicationConfiguration = applicationConfiguration;
        this.paymentsApi = paymentsApi;
    }

    // Step 0
    @GetMapping("/hello-world")
    public ResponseEntity<String> helloWorld() throws Exception {
        return ResponseEntity.ok().body("This is the 'Hello World' from the workshop - You've successfully finished step 0!");
    }

    // Step 7
    @PostMapping("/api/paymentMethods")
    public ResponseEntity<PaymentMethodsResponse> paymentMethods() throws IOException, ApiException {
        var paymentMethodsRequest = new PaymentMethodsRequest();
        paymentMethodsRequest.setMerchantAccount(applicationConfiguration.getAdyenMerchantAccount());

        log.info("Retrieving available Payment Methods from Adyen {}", paymentMethodsRequest);
        var response = paymentsApi.paymentMethods(paymentMethodsRequest);
        log.info("Payment Methods response from Adyen {}", response);
        return ResponseEntity.ok().body(response);
    }

    // Step 9 - Implement the /payments call to Adyen.
    @PostMapping("/api/payments")
    public ResponseEntity<PaymentResponse> payments(@RequestHeader String host, @RequestBody PaymentRequest body, HttpServletRequest request) throws IOException, ApiException {
        var paymentRequest = new PaymentRequest();

        var amount = new Amount()
                .currency("EUR")
                .value(9998L);
        paymentRequest.setAmount(amount);
        paymentRequest.setMerchantAccount(applicationConfiguration.getAdyenMerchantAccount());
        paymentRequest.setChannel(PaymentRequest.ChannelEnum.WEB);

        paymentRequest.setPaymentMethod(body.getPaymentMethod());

        var orderRef = UUID.randomUUID().toString();
        paymentRequest.setReference(orderRef);
        host = "opulent-halibut-jr4qj6gqx7cjj9-8080.app.github.dev";
        host = host.replaceFirst(":\\d+$", ""); // Remove port if present
        
        // The returnUrl field basically means: Once done with the payment, where should the application redirect you?
        paymentRequest.setReturnUrl(request.getScheme() + "://" + host + "/handleShopperRedirect?orderRef=" + orderRef); // Example: Turns into http://localhost:8080/handleShopperRedirect?orderRef=354fa90e-0858-4d2f-92b9-717cb8e18173
        
        log.info("******* ReturnUrl = " + paymentRequest.getReturnUrl() + "");
        log.info("PaymentRequest {}", paymentRequest);

        // Step 11 - Add the idempotency key
        var requestOptions = new RequestOptions();
        requestOptions.setIdempotencyKey(UUID.randomUUID().toString());


        // Step 12 3DS2 Redirect - Add the following additional parameters to your existing payment request for 3DS2 Redirect:
        // Note: Visa requires additional properties to be sent in the request, see documentation for Redirect 3DS2: https://docs.adyen.com/online-payments/3d-secure/redirect-3ds2/web-drop-in/#make-a-payment
        var authenticationData = new AuthenticationData();
        authenticationData.setAttemptAuthentication(AuthenticationData.AttemptAuthenticationEnum.ALWAYS);
        paymentRequest.setAuthenticationData(authenticationData);

        // Add the following lines, if you want to enable the Native 3DS2 flow:
        // Note: Visa requires additional properties to be sent in the request, see documentation for Native 3DS2: https://docs.adyen.com/online-payments/3d-secure/native-3ds2/web-drop-in/#make-a-payment
        //authenticationData.setThreeDSRequestData(new ThreeDSRequestData().nativeThreeDS(ThreeDSRequestData.NativeThreeDSEnum.PREFERRED));
        //paymentRequest.setAuthenticationData(authenticationData);

        paymentRequest.setOrigin(request.getScheme() + "://" + host);
        paymentRequest.setBrowserInfo(body.getBrowserInfo());
        paymentRequest.setShopperIP(request.getRemoteAddr());
        paymentRequest.setShopperInteraction(PaymentRequest.ShopperInteractionEnum.ECOMMERCE);


        var billingAddress = new BillingAddress();
        billingAddress.setCity("Amsterdam");
        billingAddress.setCountry("NL");
        billingAddress.setPostalCode("1012KK");
        billingAddress.setStreet("Rokin");
        billingAddress.setHouseNumberOrName("49");
        paymentRequest.setBillingAddress(billingAddress);

        log.info("PaymentsRequest {}", paymentRequest);
        var response = paymentsApi.payments(paymentRequest, requestOptions); // Notice how we're adding this property to our existing code*
        log.info("PaymentsResponse {}", response);
        return ResponseEntity.ok().body(response);


    }

    // Step 13 - Handle details call (triggered after Native 3DS2 flow)
    @PostMapping("/api/payments/details")
    public ResponseEntity<PaymentDetailsResponse> paymentsDetails(@RequestBody PaymentDetailsRequest detailsRequest) throws IOException, ApiException
    {
        log.info("***** In api payment details *****");
        log.info("PaymentDetailsRequest {}", detailsRequest);
        var response = paymentsApi.paymentsDetails(detailsRequest);
        log.info("PaymentDetailsResponse {}", response);
        return ResponseEntity.ok().body(null);
    }

   // Step 14 - Handle Redirect 3DS2 during payment.
    @GetMapping("/handleShopperRedirect")
    public RedirectView redirect(@RequestParam(required = false) String payload, @RequestParam(required = false) String redirectResult) throws IOException, ApiException {
        var paymentDetailsRequest = new PaymentDetailsRequest();

        log.info("******* In handleShooperRedirect *******");
        log.info(redirectResult);
        

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

        // Handle response and redirect user accordingly
        //var redirectURL = "/result/";
        //redirectURL = redirectURL.replaceFirst(":\\d+$", ""); // Remove port if present
        //log.info("********* " + redirectURL + " **********");

        //host = "opulent-halibut-jr4qj6gqx7cjj9-8080.app.github.dev";
        var redirectURL = "opulent-halibut-jr4qj6gqx7cjj9-8080.app.github.dev/result/";


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
        // https://opulent-halibut-jr4qj6gqx7cjj9-8080.app.github.dev:8080/result/success?reason=Authorised
        RedirectView myredirectView = new RedirectView();
        myredirectView.setUrl(redirectURL);
        log.info("********* " + redirectURL + " **********");
        redirectURL = redirectURL + "?reason=" + paymentsDetailsResponse.getResultCode();
        myredirectView.setUrl(redirectURL);
        log.info("********* RedirectView URL : " + myredirectView.getUrl() + " **********");
        return myredirectView;
    }

    

}
