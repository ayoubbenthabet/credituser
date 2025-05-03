package com.example.BACK.controller;

import com.example.BACK.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/sendCode")
    public String sendResetCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("L'email est requis !");
        }
        return emailService.envoyerCode(email);
    }


    @PostMapping("/verifyCode")
    public String verifyCode(@RequestParam String email, @RequestParam String code) {
        if (emailService.verifierCode(email, code)) {
            return "Code valide ! Vous pouvez maintenant réinitialiser votre mot de passe.";
        } else {
            return "Code invalide ou expiré.";
        }
    }
}
