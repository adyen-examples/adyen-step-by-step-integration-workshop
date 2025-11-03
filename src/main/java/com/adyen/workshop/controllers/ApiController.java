package com.adyen.workshop.controllers;

import com.adyen.workshop.configurations.ApplicationConfiguration;

import jakarta.servlet.http.HttpServletRequest;
import java.com.adyen.com.workshop.configurations.*;
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
 * Your backend controller.
 */
@RestController
public class ApiController {
    private final Logger log = LoggerFactory.getLogger(ApiController.class);

    private final ApplicationConfiguration applicationConfiguration;

    public ApiController(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    // After running `/gradlew bootRun`, visit /hello-world
    @GetMapping("/hello-world")
    public ResponseEntity<String> helloWorld() throws Exception {
        return ResponseEntity.ok().body("This is the 'Hello World' from the workshop - You've successfully finished step 0!");
    }

    // Example endpoint
    @PostMapping("/endpointname")
    public ResponseEntity<String> payments(@RequestHeader String host, @RequestBody String body, HttpServletRequest request) throws IOException {
        // `@RequestBody String body` contains the body of the POST requests, you can also add a strongly typed object here*
        return ResponseEntity.ok().body("not implemented");
    }

}
