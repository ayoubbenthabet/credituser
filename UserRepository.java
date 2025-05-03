package com.example.BACK.repository;

import com.example.BACK.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Vérifier si un utilisateur existe par email

        User findByEmail(String email);  // Méthode pour rechercher un utilisateur par email

       User findByTel(Integer tel);


    Optional<User> findById(Long id);
    // Vérifier si un utilisateur existe par CIN
    boolean existsByCin(int cin);

    // Trouver un utilisateur par email et mot de passe
    Optional<User> findByEmailAndMdp(String email, String mdp);
}
