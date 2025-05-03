package com.example.BACK.controller;

import com.example.BACK.model.Role;
import com.example.BACK.model.User;
import com.example.BACK.repository.RoleRepository;
import com.example.BACK.service.SmsService;
import com.example.BACK.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private RoleRepository roleRepository;

    // Récupérer tous les utilisateurs
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // Récupérer les utilisateurs triés par nom
    @GetMapping("/sorted")
    public List<User> getUsersSortedByName() {
        return userService.getUsersSortedByName();
    }

    // Récupérer un utilisateur par son ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    // Enregistrer un utilisateur
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        try {
            // Vérifier le rôle comme précédemment
            Role role = roleRepository.findByNomRole(user.getRole().getNomRole())
                    .orElseThrow(() -> new IllegalArgumentException("Role not found"));

            user.setRole(role);

            return userService.registerUser(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Authentification de l'utilisateur
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String mdp = credentials.get("mdp");

        // Appeler la méthode d'authentification du service
        String response = userService.authenticateUser(email, mdp);

        // Si l'authentification réussie, on récupère le rôle de l'utilisateur
        if (response != null) {
            String role = userService.getUserRole(email);
            if ("ADMIN".equals(role)) {
                return ResponseEntity.ok("Back");
            } else if ("CLIENT".equals(role)) {
                return ResponseEntity.ok("Front");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Rôle non autorisé");
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou mot de passe incorrect");
        }
    }

    // Mettre à jour un utilisateur
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    // Supprimer un utilisateur
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Utilisateur supprimé avec succès.");
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String tel) {
        if (tel == null || tel.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid phone number");
        }

        try {
            Integer phone = Integer.valueOf(tel); // Vérifier que c'est un numéro valide
            User user = userService.findByPhoneNumber(phone);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            // Envoi du SMS avec un code de réinitialisation
            String resetCode = smsService.sendPasswordResetSms(tel);

            if (resetCode != null) {
                return ResponseEntity.ok("Reset code sent successfully via SMS.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send SMS.");
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Invalid phone number format");
        }
    }



//
//    @PostMapping("/reset-password/{id}")
//    public ResponseEntity<String> resetPassword(@PathVariable Long id,
//                                                @RequestParam String resetCode,
//                                                @RequestParam String newPassword) {
//        try {
//            String response = userService.resetPassword(id, resetCode, newPassword);
//            return ResponseEntity.ok(response); // Retourne un message de succès
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }




    @PostMapping("/reset-password/{email}")
    public ResponseEntity<String> resetPasswordByEmail(
            @PathVariable String email,
            @RequestBody Map<String, String> body) {
        String code = body.get("code");
        String newPassword = body.get("newPassword");
        userService.resetPasswordByEmail(email, code, newPassword);
        return ResponseEntity.ok("Password has been successfully reset via email.");
    }

    // Reset via phone
    @PostMapping("/reset-password-phone/{phone}")
    public ResponseEntity<String> resetPasswordByPhone(
            @PathVariable String phone,
            @RequestBody Map<String, String> body) {
        String code = body.get("code");
        String newPassword = body.get("newPassword");
        userService.resetPasswordByPhone(phone, code, newPassword);
        return ResponseEntity.ok("Password has been successfully reset via phone.");
    }


    @GetMapping("/with-credits")
    public ResponseEntity<?> getAllUsersWithCredits() {
        try {
            List<User> usersWithCredits = userService.getAllUsersWithCredits();
            return ResponseEntity.ok(usersWithCredits);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching users with credits: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/with-credits")
    public ResponseEntity<?> getUserWithCredits(@PathVariable Long id) {
        try {
            User userWithCredits = userService.getUserWithCredits(id);
            return ResponseEntity.ok(userWithCredits);
        } catch (Exception e) {
            if (e.getMessage().contains("User not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found with id: " + id);
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching user with credits: " + e.getMessage());
        }
    }


}
