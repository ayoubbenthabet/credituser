package com.example.BACK.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;




@Entity
public class ChatGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // Liste des utilisateurs dans le groupe
    @ManyToMany
    @JoinTable(
            name = "chat_group_users",
            joinColumns = @JoinColumn(name = "chat_group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonBackReference
    private List<User> users = new ArrayList<>();

    // Liste des rôles associés au groupe
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "chat_group_roles", joinColumns = @JoinColumn(name = "chat_group_id"))
    @Column(name = "roles")
    private List<String> roles = new ArrayList<>();

    // Getters et setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    // Méthode pour ajouter un utilisateur au groupe
    public void addUser(User user) {
        if (!users.contains(user)) {
            users.add(user);
            user.getGroups().add(this);
        }
    }
}
