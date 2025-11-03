package com.adyen.workshop.controllers.views;

import com.adyen.workshop.configurations.ApplicationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class ViewController {
    private final Logger log = LoggerFactory.getLogger(ViewController.class);

    private final ApplicationConfiguration applicationConfiguration;

    public ViewController(ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
    }

    // The main entry point that will be shown, see resources/static/templates/index.html
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // Example on how to pass variables using thymeleaf templates, see `resources/static/templates/result.html`
    // The {{type}} values can be: `success`, `failed`, `error`, `pending`, see `resources/static/images/{{type}}.html`
    // Visit your /result/success in your browser after running `/gradlew bootRun`
    @GetMapping("/result/{type}")
    public String result(@PathVariable String type, Model model) {
        model.addAttribute("type", type);
        //model.addAttribute("some", "example");
        return "result";
    }
}
