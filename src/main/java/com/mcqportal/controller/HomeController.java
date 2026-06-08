package com.mcqportal.controller;

import com.mcqportal.repository.CertificateRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
    private final CertificateRepository certificateRepository;

    public HomeController(CertificateRepository certificateRepository) {
        this.certificateRepository = certificateRepository;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }

    @GetMapping("/verify")
    public String verify(@RequestParam(required = false) String certificateId, Model model) {
        if (certificateId != null && !certificateId.isBlank()) {
            certificateRepository.findByCertificateId(certificateId.trim())
                    .ifPresentOrElse(certificate -> model.addAttribute("certificate", certificate),
                            () -> model.addAttribute("notFound", true));
        }
        return "certificates/verify";
    }
}
