package com.example.BACK.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Name must only contain letters")
    private String nom;

    @NotNull(message = "Surname is required")
    @Size(min = 2, max = 50, message = "Surname must be between 2 and 50 characters")
    @Pattern(regexp = "^[A-Za-z]+$", message = "Surname must only contain letters")
    private String prenom;

    @NotNull(message = "CIN is required")
    @Min(value = 10000000, message = "CIN must be an 8-digit number")
    @Max(value = 99999999, message = "CIN must be an 8-digit number")
    private int cin;

    @NotNull(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull
   // @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    @Pattern(regexp = ".*[A-Z].*", message = "Password must contain at least one uppercase letter")
    @Pattern(regexp = ".*[a-z].*", message = "Password must contain at least one lowercase letter")
    @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit")
    @Pattern(regexp = ".*[^a-zA-Z0-9].*", message = "Password must contain at least one special character")
    private String mdp;

    @NotNull(message = "Gender is required")
    @Pattern(regexp = "^(man|woman)$", message = "Gender must be 'man' or 'woman'")
    private String sexe;

    @NotNull(message = "Phone number is required")
    @Min(value = 10000000, message = "Phone number must be exactly 8 digits")
    @Max(value = 99999999, message = "Phone number must be exactly 8 digits")
    private Integer tel;

    @ManyToOne
    @JoinColumn(name = "idRole", nullable = false)
    private Role role;

    @NotNull(message = "Address is required")
    @Size(min = 5, max = 100, message = "Address must be between 5 and 100 characters")
    private String adresse;

    @Column
    private String image;

    @Column
    private Float soldeCourant = null;

    @NotNull(message = "RIB is required")
    @Pattern(regexp = "^[0-9]{20}$", message = "RIB must be exactly 20 digits")
    private String rib;

    @NotNull(message = "Age is required")
    @Min(value = 20, message = "Age must be at least 20")
    @Max(value = 60, message = "Age must be less than 60")
    private Integer age;

    private String passwordResetToken;
    private LocalDateTime passwordResetTokenExpiration;

    public String getPasswordResetToken() {
        return passwordResetToken;
    }

    public void setPasswordResetToken(String passwordResetToken) {
        this.passwordResetToken = passwordResetToken;
    }

    public LocalDateTime getPasswordResetTokenExpiration() {
        return passwordResetTokenExpiration;
    }

    public void setPasswordResetTokenExpiration(LocalDateTime passwordResetTokenExpiration) {
        this.passwordResetTokenExpiration = passwordResetTokenExpiration;
    }
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("user")
    private List<Credit> credits = new ArrayList<>();
    @ManyToMany
    @JoinTable(
            name = "chat_group_users", // 🔥 Utiliser le vrai nom de la table intermédiaire
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "chat_group_id")
    )
    @JsonBackReference
    private List<ChatGroup> groups;
    private String resetCode;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public int getCin() {
        return cin;
    }

    public void setCin(int cin) {
        this.cin = cin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMdp() {
        return mdp;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public Integer getTel() {
        return tel;
    }

    public void setTel(Integer tel) {
        this.tel = tel;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Float getSoldeCourant() {
        return soldeCourant;
    }

    public void setSoldeCourant(Float soldeCourant) {
        this.soldeCourant = soldeCourant;
    }

    public String getRib() {
        return rib;
    }

    public void setRib(String rib) {
        this.rib = rib;
    }

    public Integer getAge() {
        return age;
    }
    public String getResetCode() {
        return resetCode;
    }

    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }
    public void setAge(Integer age) {
        this.age = age;
    }

    public List<ChatGroup> getGroups() {
        return groups;
    }

    public void setGroups(List<ChatGroup> groups) {
        this.groups = groups;
    }

    public List<Credit> getCredits() { return credits; }
    public void setCredits(List<Credit> credits) { this.credits = credits; }
}
