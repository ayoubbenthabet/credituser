package com.example.BACK.repository;



import com.example.BACK.model.Credit;
import com.example.BACK.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CreditRepository extends JpaRepository<Credit, Long> {

    // New method to find credits by user
    List<Credit> findByUser(User user);
    List<Credit> findByUserAndCreditStatusNot(User user, Credit.CreditStatus status);


    // Search by gender
    List<Credit> findByGender(Credit.Gender gender);

    // Search by married status
    List<Credit> findByMarried(Credit.Married married);

    // Search by education
    List<Credit> findByEducation(Credit.Education education);

    // Search by self-employed status
    List<Credit> findBySelfEmployed(Credit.SelfEmployed selfEmployed);

    // Search by credit type
    List<Credit> findByCreditType(Credit.CreditType creditType);

    // Search by credit status
    List<Credit> findByCreditStatus(Credit.CreditStatus creditStatus);

    // Search by interest type
    List<Credit> findByInterestType(Credit.InterestType interestType);

    // Custom query for advanced search and filter
    @Query("SELECT c FROM Credit c WHERE " +
            "(:gender IS NULL OR c.gender = :gender) AND " +
            "(:married IS NULL OR c.married = :married) AND " +
            "(:education IS NULL OR c.education = :education) AND " +
            "(:selfEmployed IS NULL OR c.selfEmployed = :selfEmployed) AND " +
            "(:creditType IS NULL OR c.creditType = :creditType) AND " +
            "(:creditStatus IS NULL OR c.creditStatus = :creditStatus) AND " +
            "(:interestType IS NULL OR c.interestType = :interestType) AND " +
            "(:minLoanAmount IS NULL OR c.loanAmount >= :minLoanAmount) AND " +
            "(:maxLoanAmount IS NULL OR c.loanAmount <= :maxLoanAmount) AND " +
            "(:minLoanTerm IS NULL OR c.loanTerm >= :minLoanTerm) AND " +
            "(:maxLoanTerm IS NULL OR c.loanTerm <= :maxLoanTerm)")
    List<Credit> searchAndFilterCredits(
            @Param("gender") Credit.Gender gender,
            @Param("married") Credit.Married married,
            @Param("education") Credit.Education education,
            @Param("selfEmployed") Credit.SelfEmployed selfEmployed,
            @Param("creditType") Credit.CreditType creditType,
            @Param("creditStatus") Credit.CreditStatus creditStatus,
            @Param("interestType") Credit.InterestType interestType,
            @Param("minLoanAmount") Double minLoanAmount,
            @Param("maxLoanAmount") Double maxLoanAmount,
            @Param("minLoanTerm") Integer minLoanTerm,
            @Param("maxLoanTerm") Integer maxLoanTerm
    );

    // Custom query for advanced search and filter by user
    @Query("SELECT c FROM Credit c WHERE " +
            "c.user = :user AND " +
            "(:gender IS NULL OR c.gender = :gender) AND " +
            "(:married IS NULL OR c.married = :married) AND " +
            "(:education IS NULL OR c.education = :education) AND " +
            "(:selfEmployed IS NULL OR c.selfEmployed = :selfEmployed) AND " +
            "(:creditType IS NULL OR c.creditType = :creditType) AND " +
            "(:creditStatus IS NULL OR c.creditStatus = :creditStatus) AND " +
            "(:interestType IS NULL OR c.interestType = :interestType) AND " +
            "(:minLoanAmount IS NULL OR c.loanAmount >= :minLoanAmount) AND " +
            "(:maxLoanAmount IS NULL OR c.loanAmount <= :maxLoanAmount) AND " +
            "(:minLoanTerm IS NULL OR c.loanTerm >= :minLoanTerm) AND " +
            "(:maxLoanTerm IS NULL OR c.loanTerm <= :maxLoanTerm)")
    List<Credit> searchAndFilterCreditsByUser(
            @Param("user") User user,
            @Param("gender") Credit.Gender gender,
            @Param("married") Credit.Married married,
            @Param("education") Credit.Education education,
            @Param("selfEmployed") Credit.SelfEmployed selfEmployed,
            @Param("creditType") Credit.CreditType creditType,
            @Param("creditStatus") Credit.CreditStatus creditStatus,
            @Param("interestType") Credit.InterestType interestType,
            @Param("minLoanAmount") Double minLoanAmount,
            @Param("maxLoanAmount") Double maxLoanAmount,
            @Param("minLoanTerm") Integer minLoanTerm,
            @Param("maxLoanTerm") Integer maxLoanTerm
    );
}