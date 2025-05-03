package com.example.BACK.service;

import com.example.BACK.model.ChatGroup;
import com.example.BACK.model.User;
import com.example.BACK.repository.ChatGroupRepository;
import com.example.BACK.repository.RoleRepository;
import com.example.BACK.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.transaction.annotation.Transactional;

import lombok.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserService {
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ChatGroupRepository groupRepository;

    @Autowired
    private ChatGroupRepository chatGroupRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersSortedByName() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "nom"));
    }

    public User findByPhoneNumber(Integer phoneNumber) {
        return userRepository.findByTel(phoneNumber);
    }

    @Transactional
    public User getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ⚠️ Initialisation explicite de la collection avant fermeture de la session
        user.getGroups().size();

        return user;
    }

    @Transactional
    public List<String> getUserGroupsById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Récupérer uniquement les noms des groupes
        return user.getGroups().stream()
                .map(ChatGroup::getName) // Utilisation correcte
                .collect(Collectors.toList());
    }

    public void savePasswordResetToken(User user, String token) {
        // Vous pouvez créer un objet pour stocker le token et l'expiration, ou le stocker directement dans l'utilisateur
        // Assurez-vous d'avoir une table pour les tokens de réinitialisation de mot de passe si nécessaire
        user.setPasswordResetToken(token);
        user.setPasswordResetTokenExpiration(LocalDateTime.now().plusHours(1)); // Exemple d'expiration dans une heure
        userRepository.save(user);
    }

    public ResponseEntity<?> registerUser(User user) {
        // Vérifier si l'email existe déjà
        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            return ResponseEntity.badRequest().body("Email already in use.");
        }

        // Validation de l'email
        if (!isValidEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("Invalid email format.");
        }

        // Vérifier si le CIN existe déjà
        if (userRepository.existsByCin(user.getCin())) {
            return ResponseEntity.badRequest().body("CIN already in use.");
        }

        // Validation du CIN
        if (!isValidCin(user.getCin())) {
            return ResponseEntity.badRequest().body("CIN should be numeric and exactly 8 digits.");
        }

        // Validation de l'âge
        if (!isValidAge(user.getAge())) {
            return ResponseEntity.badRequest().body("Age must be between 20 and 60.");
        }

        // Validation du nom et prénom
        if (!isValidName(user.getNom())) {
            return ResponseEntity.badRequest().body("Name should only contain letters.");
        }

        if (!isValidName(user.getPrenom())) {
            return ResponseEntity.badRequest().body("Surname should only contain letters.");
        }

        // Validation du mot de passe
        if (!isStrongPassword(user.getMdp())) {
            return ResponseEntity.badRequest().body("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one number.");
        }

        // Validation du sexe
        if (!isValidSex(user.getSexe())) {
            return ResponseEntity.badRequest().body("Sex must be either 'man' or 'woman'.");
        }

        // Validation du numéro de téléphone
        if (!isValidPhone(user.getTel())) {
            return ResponseEntity.badRequest().body("Phone number must be numeric and exactly 8 digits.");
        }

        // Vérification de l'adresse
        if (user.getAdresse() == null || user.getAdresse().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Address cannot be empty.");
        }

        // Sauvegarde du mot de passe en clair pour l'email
        String plainPassword = user.getMdp(); // Le mot de passe en clair avant cryptage

        // Cryptage du mot de passe
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPassword = passwordEncoder.encode(user.getMdp());
        user.setMdp(encryptedPassword);

        // Sauvegarde de l'utilisateur dans la base de données
        userRepository.save(user);

        // Envoi de l'email de confirmation avec le mot de passe en clair
        sendConfirmationEmail(user, plainPassword);

        // Ajouter l'utilisateur au groupe correspondant en fonction de son rôle
        addUserToGroup(user); // Ajout de l'utilisateur au groupe ici

        // Retour de la réponse après l'enregistrement
        return ResponseEntity.ok("User registered successfully.");
    }

    public void addUserToGroup(User user) {
        List<ChatGroup> allGroups = chatGroupRepository.findAll(); // Récupère tous les groupes

        for (ChatGroup group : allGroups) {
            if (group.getRoles().contains(user.getRole().getNomRole())) { // Vérifie si le rôle est autorisé
                group.getUsers().add(user);
            }
        }

        userRepository.save(user); // Sauvegarde l'utilisateur avec ses nouveaux groupes
    }

    // Méthode d'envoi d'email de confirmation
    private void sendConfirmationEmail(User user, String plainPassword) {
        String subject = "Your Registration Confirmation";
        String text = "Hello " + user.getPrenom() + ",\n\n" +
                "Your registration has been successful. Here are your details:\n" +
                "Email: " + user.getEmail() + "\n" +
                "Password: " + plainPassword + "\n\n" +
                "Thank you for choosing us.";

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String authenticateUser(String email, String mdp) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (passwordEncoder.matches(mdp, user.getMdp())) {
                return "Authenticated";
            }
        }
        return null;
    }

    public String getUserRole(String email) {
        User user = userRepository.findByEmail(email);
        return user != null ? user.getRole().getNomRole() : null;
    }

    public User updateUser(Long userId, User updatedUser) {
        // Récupérer l'utilisateur existant
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Mise à jour des champs de l'utilisateur
        if (updatedUser.getEmail() != null && !updatedUser.getEmail().isEmpty()) {
            user.setEmail(updatedUser.getEmail());
        }

        if (updatedUser.getNom() != null && !updatedUser.getNom().isEmpty()) {
            user.setNom(updatedUser.getNom());
        }

        if (updatedUser.getPrenom() != null && !updatedUser.getPrenom().isEmpty()) {
            user.setPrenom(updatedUser.getPrenom());
        }

        // Vérification si l'âge n'est pas null
        if (updatedUser.getAge() != null) {
            user.setAge(updatedUser.getAge());
        }

        if (updatedUser.getAdresse() != null && !updatedUser.getAdresse().isEmpty()) {
            user.setAdresse(updatedUser.getAdresse());
        }

        // Vérification si le téléphone (tel) est renseigné (en supposant que tel peut être 0 lorsqu'il est vide)
        if (updatedUser.getTel() != 0) { // Vérifier si 'tel' n'est pas égal à 0
            user.setTel(updatedUser.getTel());
        }

        if (updatedUser.getRib() != null && !updatedUser.getRib().isEmpty()) {
            user.setRib(updatedUser.getRib());
        }

        if (updatedUser.getSexe() != null && !updatedUser.getSexe().isEmpty()) {
            user.setSexe(updatedUser.getSexe());
        }

        if (updatedUser.getImage() != null && !updatedUser.getImage().isEmpty()) {
            user.setImage(updatedUser.getImage());
        }

        // Vérification si le solde courant n'est pas null
        if (updatedUser.getSoldeCourant() != null) {
            user.setSoldeCourant(updatedUser.getSoldeCourant());
        }

        // Mise à jour du mot de passe
        if (updatedUser.getMdp() != null && !updatedUser.getMdp().isEmpty()) {
            // Vérification de la validité du mot de passe
            if (!isStrongPassword(updatedUser.getMdp())) {
                System.out.println("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one number.");
                throw new IllegalArgumentException("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, and one number.");
            }

            // Crypter le mot de passe
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String encryptedPassword = passwordEncoder.encode(updatedUser.getMdp());
            user.setMdp(encryptedPassword);
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPassword = passwordEncoder.encode(user.getMdp());
        user.setMdp(encryptedPassword); // Le mot de passe crypté est sauvegardé dans l'entité User

        // Sauvegarder l'utilisateur mis à jour dans la base de données
        userRepository.save(user);

        return user;
    }




    public String resetPassword(Long userId, String resetCode, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!resetCode.equals(user.getResetCode())) {
            throw new IllegalArgumentException("Invalid reset code");
        }

        // Hachage du mot de passe avant de le stocker
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setMdp(hashedPassword);  // Utilisation de setMdp pour mettre à jour le mot de passe
        user.setResetCode(null); // Réinitialiser le code de réinitialisation

        userRepository.save(user);
        return "Password successfully updated";
    }

    public boolean deleteUser(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Dissocier l'utilisateur de tous les groupes auxquels il appartient
            for (ChatGroup group : user.getGroups()) {
                group.getUsers().remove(user); // Retirer l'utilisateur du groupe
                chatGroupRepository.save(group); // Sauvegarder le groupe mis à jour
            }

            userRepository.deleteById(id); // Supprimer l'utilisateur de la base de données
            return true;
        }
        return false;
    }

    private boolean isValidEmail(String email) {
        return email != null && email.contains("@");
    }

    private boolean isValidCin(int cin) {
        return String.valueOf(cin).length() == 8;
    }

    private boolean isValidAge(int age) {
        return age >= 20 && age <= 60;
    }

    private boolean isValidName(String name) {
        return name != null && name.matches("^[A-Za-z]+$");
    }

    private boolean isStrongPassword(String password) {
        return password.length() >= 8 && Pattern.compile("[A-Z]").matcher(password).find()
                && Pattern.compile("[a-z]").matcher(password).find() && Pattern.compile("\\d").matcher(password).find();
    }

    private boolean isValidSex(String sexe) {
        return sexe != null && (sexe.equals("man") || sexe.equals("woman"));
    }

    private boolean isValidPhone(Integer tel) {
        return tel != null && String.valueOf(tel).length() == 8;
    }




    // 🔹 Réinitialisation par Email
    public void resetPasswordByEmail(String email, String code, String newPassword) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Vous devez maintenant gérer le code de réinitialisation d'une autre manière,
        // car `resetCodeRepository` n'est pas disponible.
        // Assurez-vous d'avoir une logique appropriée pour vérifier ce code
        // Par exemple : récupérer un code stocké ailleurs dans une base de données.

        // Si le code est valide, vous pouvez changer le mot de passe
        if (isValidCode(code)) { // Remplacez cette fonction par votre propre logique
            // Hachage du mot de passe
            user.setMdp(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new RuntimeException("Invalid reset code");
        }
    }

    // 🔹 Réinitialisation par Téléphone
    public void resetPasswordByPhone(String phone, String code, String newPassword) {
        // Conversion du numéro de téléphone en Integer
        Integer phoneNumber = null;
        try {
            phoneNumber = Integer.parseInt(phone); // Conversion de String en Integer
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid phone number format");
        }

        User user = userRepository.findByTel(phoneNumber); // Utiliser l'Integer
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Logique de vérification du code de réinitialisation (à adapter comme pour l'email)
        if (isValidCode(code)) { // Remplacez cette fonction par votre propre logique
            // Hachage du mot de passe
            user.setMdp(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        } else {
            throw new RuntimeException("Invalid reset code");
        }
    }

    // Fonction fictive pour valider un code
    private boolean isValidCode(String code) {
        // Votre logique de validation du code ici
        return true;  // Remplacez cette ligne par une vraie validation
    }



    @Transactional(readOnly = true)
    public List<User> getAllUsersWithCredits() {
        logger.info("Fetching all users with their credits");
        List<User> users = userRepository.findAll();

        // Initialize the credits collection for each user
        for (User user : users) {
            if (user.getCredits() != null) {
                user.getCredits().size(); // Force initialization of the lazy collection
                logger.info("User ID {} has {} credits", user.getId(), user.getCredits().size());
            }
        }

        return users;
    }

    @Transactional(readOnly = true)
    public User getUserWithCredits(Long userId) {
        logger.info("Fetching user ID {} with credits", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new RuntimeException("User not found with id: " + userId);
                });

        // Initialize the credits collection
        if (user.getCredits() != null) {
            user.getCredits().size(); // Force initialization of the lazy collection
            logger.info("User ID {} has {} credits", user.getId(), user.getCredits().size());
        }

        return user;
    }



}
