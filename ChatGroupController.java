package com.example.BACK.controller;

import com.example.BACK.model.ChatGroup;
import com.example.BACK.model.User;
import com.example.BACK.service.ChatGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat-groups")
public class ChatGroupController {

    @Autowired
    private ChatGroupService chatGroupService;

    /**
     * Crée un nouveau groupe de discussion.
     *
     * @param chatGroup Le groupe à créer.
     * @return Le groupe créé.
     */
    @PostMapping
    public ResponseEntity<ChatGroup> createGroup(@RequestBody ChatGroup chatGroup) {
        ChatGroup newGroup = chatGroupService.createGroup(chatGroup);
        return new ResponseEntity<>(newGroup, HttpStatus.CREATED);
    }

    /**
     * Récupère un groupe par son identifiant.
     *
     * @param id L'identifiant du groupe.
     * @return Le groupe trouvé, ou une erreur 404 si non trouvé.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ChatGroup> getGroupById(@PathVariable Long id) {
        ChatGroup chatGroup = chatGroupService.getGroupById(id);
        return chatGroup != null ? ResponseEntity.ok(chatGroup) : ResponseEntity.notFound().build();
    }

    /**
     * Récupère tous les groupes de discussion.
     *
     * @return Liste de tous les groupes.
     */
    @GetMapping
    public ResponseEntity<List<ChatGroup>> getAllGroups() {
        List<ChatGroup> groups = chatGroupService.getAllGroups();
        return ResponseEntity.ok(groups);
    }

    /**
     * Supprime un groupe par son ID.
     *
     * @param id L'identifiant du groupe à supprimer.
     * @return `204 No Content` si suppression réussie, sinon `404 Not Found`.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Long id) {
        boolean isDeleted = chatGroupService.deleteGroup(id);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * Récupère la liste des utilisateurs d'un groupe spécifique.
     *
     * @param id L'identifiant du groupe.
     * @return Liste des utilisateurs du groupe ou un message d'erreur si le groupe est vide.
     */
    @GetMapping("/{id}/users")
    public ResponseEntity<?> getUsersByGroup(@PathVariable Long id) {
        List<User> users = chatGroupService.getUsersByGroupId(id);
        if (users.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Aucun utilisateur trouvé pour ce groupe !");
        }
        return ResponseEntity.ok(users);
    }
}
