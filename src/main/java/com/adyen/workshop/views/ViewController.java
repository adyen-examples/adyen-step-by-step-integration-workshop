package com.adyen.workshop.views;

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

    private final ApplicationConfiguration applicationProperties;

    public ViewController(ApplicationConfiguration applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/preview")
    public String preview(@RequestParam String type, Model model) {
        model.addAttribute("type", type);
        return "preview";
    }

    @GetMapping("/checkout")
    public String checkout(@RequestParam String type, Model model) {
        model.addAttribute("type", type);
        model.addAttribute("clientKey", this.applicationProperties.getAdyenClientKey());
        return "checkout";
    }

    @GetMapping("/result/{type}")
    public String result(@PathVariable String type, Model model) {
        model.addAttribute("type", type);
        return "result";
    }

    @GetMapping("/redirect")
    public String redirect(Model model) {
        model.addAttribute("clientKey", this.applicationProperties.getAdyenClientKey());
        return "redirect";
    }
}
