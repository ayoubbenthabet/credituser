package com.example.BACK.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.BACK.model.Immobilier;

@Repository
public interface ImmobilierRepository extends JpaRepository<Immobilier, Long> {
}
