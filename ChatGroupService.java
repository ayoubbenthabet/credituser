package com.example.BACK.service;

import com.example.BACK.model.ChatGroup;
import com.example.BACK.model.User;
import com.example.BACK.repository.ChatGroupRepository;
import com.example.BACK.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ChatGroupService {

    @Autowired
    private ChatGroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Ajoute un utilisateur au groupe approprié en fonction de son rôle.
     * Si le rôle de l'utilisateur correspond à un rôle d'un groupe existant,
     * l'utilisateur est ajouté à ce groupe.
     *
     * @param user L'utilisateur à ajouter au groupe.
     */
    public void addUserToGroup(User user) {
        List<ChatGroup> groups = groupRepository.findAll();

        for (ChatGroup group : groups) {
            if (group.getRoles().contains(user.getRole().getNomRole())) {
                group.getUsers().add(user);
                groupRepository.save(group);
                break;
            }
        }
    }

    /**
     * Récupère la liste des utilisateurs d'un groupe spécifique.
     *
     * @param groupId L'identifiant du groupe.
     * @return Liste des utilisateurs du groupe.
     */
    public List<User> getUsersByGroupId(Long groupId) {
        Optional<ChatGroup> group = groupRepository.findById(groupId);
        return group.map(ChatGroup::getUsers).orElse(Collections.emptyList());
    }

    /**
     * Récupère tous les groupes de discussion.
     *
     * @return Liste de tous les groupes de discussion.
     */
    public List<ChatGroup> getAllGroups() {
        return groupRepository.findAll();
    }

    /**
     * Récupère un groupe par son identifiant.
     *
     * @param groupId L'identifiant du groupe.
     * @return Le groupe correspondant à l'identifiant, ou null si non trouvé.
     */
    public ChatGroup getGroupById(Long groupId) {
        return groupRepository.findById(groupId).orElse(null);
    }

    /**
     * Crée un nouveau groupe de discussion.
     *
     * @param group Le groupe à créer.
     * @return Le groupe créé.
     */
    public ChatGroup createGroup(ChatGroup group) {
        return groupRepository.save(group);
    }

    /**
     * Supprime un groupe de discussion.
     *
     * @param id L'identifiant du groupe à supprimer.
     * @return `true` si la suppression a réussi, `false` sinon.
     */
    public boolean deleteGroup(Long id) {
        Optional<ChatGroup> groupOptional = groupRepository.findById(id);
        if (groupOptional.isPresent()) {
            groupRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Récupère un groupe par son nom.
     *
     * @param name Le nom du groupe.
     * @return Le groupe correspondant au nom, ou null si non trouvé.
     */
    public ChatGroup getGroupByName(String name) {
        return groupRepository.findByName(name).orElse(null);
    }

    /**
     * Modifie les informations d'un groupe existant.
     *
     * @param groupId L'identifiant du groupe à modifier.
     * @param group   Le groupe avec les nouvelles informations.
     * @return Le groupe mis à jour, ou null si non trouvé.
     */
    public ChatGroup updateGroup(Long groupId, ChatGroup group) {
        return groupRepository.findById(groupId)
                .map(existingGroup -> {
                    existingGroup.setName(group.getName());
                    existingGroup.setRoles(group.getRoles());
                    return groupRepository.save(existingGroup);
                })
                .orElse(null);
    }
}
