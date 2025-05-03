package com.example.BACK.repository;

import com.example.BACK.model.ResetCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetCodeRepository extends JpaRepository<ResetCode, Long> {

    Optional<ResetCode> findByEmail(String email);

    Optional<ResetCode> findByPhone(String phone);
}
