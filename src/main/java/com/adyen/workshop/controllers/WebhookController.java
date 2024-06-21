package com.adyen.workshop.controllers;

import com.adyen.model.notification.NotificationRequest;
import com.adyen.model.notification.NotificationRequestItem;
import com.adyen.util.HMACValidator;
import com.adyen.workshop.configurations.ApplicationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.SignatureException;

/**
 * REST controller for receiving Adyen webhook notifications
 */
@CrossOrigin
@RestController
public class WebhookController {
    private final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final ApplicationConfiguration applicationConfiguration;

    private final HMACValidator hmacValidator;

    @Autowired
    public WebhookController(ApplicationConfiguration applicationConfiguration, HMACValidator hmacValidator) {
        this.applicationConfiguration = applicationConfiguration;
        this.hmacValidator = hmacValidator;
    }

    @PostMapping("/api/webhooks/notifications")
    public ResponseEntity<String> webhooks(@RequestBody String json) throws Exception {
        log.info("/api/webhooks/notifications");
        // from string to JSON
        var notificationRequest = NotificationRequest.fromJson(json);
        // load first (and only) event
        var notificationRequestItem = notificationRequest.getNotificationItems().stream().findFirst();

        try {
            if (!notificationRequestItem.isPresent()) {
                log.warn("Empty NotificationItem");
                return ResponseEntity.noContent().build();
            }

            NotificationRequestItem item = notificationRequestItem.get();

            if (!hmacValidator.validateHMAC(item, this.applicationConfiguration.getAdyenHmacKey())) {
                log.warn("Could not validate HMAC signature for incoming webhook message: {}", item);
                return ResponseEntity.unprocessableEntity().build();
            }

            log.info("""
                            Received webhook with event {} \s
                            Merchant Reference: {}
                            PSP reference : {}""",
                    item.getEventCode(),
                    item.getMerchantReference(),
                    item.getPspReference());

            return ResponseEntity.accepted().build();
        } catch (SignatureException e) {
            // Handle invalid signature
            log.error(e.getMessage(), e);
            return ResponseEntity.unprocessableEntity().build();
        } catch (Exception e) {
            // Handle all other errors
            log.error(e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }
}