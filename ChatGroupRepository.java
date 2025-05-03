package com.example.BACK.repository;

import com.example.BACK.model.ChatGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChatGroupRepository extends JpaRepository<ChatGroup, Long> {

    // Ajoutez cette méthode pour permettre la recherche par nom
    Optional<ChatGroup> findByName(String name);
    void deleteByUsers_Id(Long userId);


}
