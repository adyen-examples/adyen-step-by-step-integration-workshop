package com.adyen.workshop.configurations;

import com.adyen.Client;
import com.adyen.Config;
import com.adyen.enums.Environment;
import com.adyen.service.checkout.PaymentsApi;
import com.adyen.util.HMACValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DependencyInjectionConfiguration {
    private final ApplicationConfiguration applicationConfiguration;

    public DependencyInjectionConfiguration(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    @Bean
    Client client() {
        var config = new Config();
        // Step 4.
        config.setApiKey(applicationConfiguration.getAdyenApiKey());
        config.setEnvironment(Environment.TEST);
        return new Client(config);
    }

    @Bean
    PaymentsApi paymentsApi(){
        return new PaymentsApi(client());
    }

    @Bean
    HMACValidator hmacValidator() {
        return new HMACValidator();
    }
}
