package com.example.BACK.service;


import com.example.BACK.model.Credit;
import com.example.BACK.model.Remboursement;
import com.example.BACK.model.User;
import com.example.BACK.repository.CreditRepository;
import com.example.BACK.repository.RemboursementRepository;
import com.example.BACK.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CreditService {

    private static final Logger logger = LoggerFactory.getLogger(CreditService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CreditRepository creditRepository;

    @Autowired
    private RemboursementRepository remboursementRepository;
    @Autowired
    private PredictionService predictionService;

    @Transactional
    public Credit createCredit(Credit credit) {
        logger.info("Creating new credit: {}", credit);
        credit.setCreditStatus(Credit.CreditStatus.PENDING);
        Credit savedCredit = creditRepository.save(credit);
        logger.info("Saved credit with ID: {}", savedCredit.getCreditId());
        List<Remboursement> repaymentSchedule = getRepaymentSchedule(savedCredit);
        savedCredit.getRemboursements().addAll(repaymentSchedule);
        for (Remboursement remboursement : repaymentSchedule) {
            remboursement.setCredit(savedCredit);
        }
        return creditRepository.save(savedCredit);
    }
    @Transactional
    public Credit createCredit(Credit credit, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new RuntimeException("User not found with id: " + userId);
                });
        if (hasActiveCredits(userId)) {
            logger.warn("User ID {} already has active credits. New credit request denied.", userId);
            throw new RuntimeException("Cannot create a new credit. You must finish your existing credit first.");
        }
        // Validate loan amount against income
        Double monthlyIncome = credit.getIncome() / 12.0; // Convert annual income to monthly
        Double maxLoanAmount = calculateMaxLoanAmount(monthlyIncome, credit.getLoanTerm());
        if (credit.getLoanAmount() > maxLoanAmount) {
            logger.warn("Requested loan amount {} exceeds maximum allowed amount {} for user ID {}",
                    credit.getLoanAmount(), maxLoanAmount, userId);
            throw new RuntimeException("The requested loan amount exceeds the maximum allowed based on your income and loan term. Maximum allowed: " + maxLoanAmount);
        }
        credit.setUser(user);
        credit.setCreditStatus(Credit.CreditStatus.PENDING);

        Credit savedCredit = creditRepository.save(credit);
        logger.info("Saved credit with ID: {} for user ID: {}", savedCredit.getCreditId(), userId);
        List<Remboursement> repaymentSchedule = getRepaymentSchedule(savedCredit);
        savedCredit.getRemboursements().addAll(repaymentSchedule);
        for (Remboursement remboursement : repaymentSchedule) {
            remboursement.setCredit(savedCredit);
        }
        return creditRepository.save(savedCredit);
    }

    @Transactional(readOnly = true)
    public Credit getCreditById(Long id) {
        logger.info("Fetching credit with ID: {}", id);
        Credit credit = creditRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Credit not found with ID: {}", id);
                    return new RuntimeException("Credit not found with id: " + id);
                });
        // Do not fetch or set remboursements here
        return credit;
    }

    @Transactional(readOnly = true)
    public Credit getCreditByIdWithRemboursements(Long id) {
        logger.info("Fetching credit with ID: {} (with remboursements)", id);
        Credit credit = creditRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Credit not found with ID: {}", id);
                    return new RuntimeException("Credit not found with id: " + id);
                });
        // Force Hibernate to initialize the remboursements list within the transaction
        credit.getRemboursements().size();
        return credit;
    }

    @Transactional(readOnly = true)
    public List<Credit> getAllCredits() {
        logger.info("Fetching all credits");
        List<Credit> credits = creditRepository.findAll();
        // Force Hibernate to initialize the remboursements list for each credit
        for (Credit credit : credits) {
            credit.getRemboursements().size();
        }
        return credits;
    }
    @Transactional(readOnly = true)
    public List<Credit> getCreditsByUserId(Long userId) {
        logger.info("Fetching credits for user ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new RuntimeException("User not found with id: " + userId);
                });

        List<Credit> credits = creditRepository.findByUser(user);
        // Force Hibernate to initialize the remboursements list for each credit
        for (Credit credit : credits) {
            credit.getRemboursements().size();
        }
        logger.info("Found {} credits for user ID: {}", credits.size(), userId);
        return credits;
    }

    @Transactional
    public Credit updateCredit(Long id, Credit creditDetails) {
        logger.info("Updating credit with ID: {}", id);
        logger.info("Received credit details: {}", creditDetails);
        Credit credit = getCreditById(id);
        logger.info("Fetched existing credit: {}", credit);

        // Update fields only if they are not null
        if (creditDetails.getGender() != null) {
            credit.setGender(creditDetails.getGender());
            logger.info("Updated gender: {}", credit.getGender());
        }
        if (creditDetails.getMarried() != null) {
            credit.setMarried(creditDetails.getMarried());
            logger.info("Updated married: {}", credit.getMarried());
        }
        if (creditDetails.getEducation() != null) {
            credit.setEducation(creditDetails.getEducation());
            logger.info("Updated education: {}", credit.getEducation());
        }
        if (creditDetails.getSelfEmployed() != null) {
            credit.setSelfEmployed(creditDetails.getSelfEmployed());
            logger.info("Updated selfEmployed: {}", credit.getSelfEmployed());
        }
        if (creditDetails.getIncome() != null) {
            credit.setIncome(creditDetails.getIncome());
            logger.info("Updated income: {}", credit.getIncome());
        }
        if (creditDetails.getLoanAmount() != null) {
            credit.setLoanAmount(creditDetails.getLoanAmount());
            logger.info("Updated loanAmount: {}", credit.getLoanAmount());
        }
        if (creditDetails.getLoanTerm() != null) {
            credit.setLoanTerm(creditDetails.getLoanTerm());
            logger.info("Updated loanTerm: {}", credit.getLoanTerm());
        }
        if (creditDetails.getCreditType() != null) {
            credit.setCreditType(creditDetails.getCreditType());
            logger.info("Updated creditType: {}", credit.getCreditType());
        }
        if (creditDetails.getCreditStatus() != null) {
            credit.setCreditStatus(creditDetails.getCreditStatus());
            logger.info("Updated creditStatus: {}", credit.getCreditStatus());
        }
        if (creditDetails.getInterestType() != null) {
            credit.setInterestType(creditDetails.getInterestType());
            logger.info("Updated interestType: {}", credit.getInterestType());
        }

        if (creditDetails.getUser() != null && creditDetails.getUser().getId() != null) {
            User user = userRepository.findById(creditDetails.getUser().getId())
                    .orElseThrow(() -> {
                        logger.error("User not found with ID: {}", creditDetails.getUser().getId());
                        return new RuntimeException("User not found with id: " + creditDetails.getUser().getId());
                    });
            credit.setUser(user);
            logger.info("Updated user: {}", user.getId());
        }
        if (creditDetails.getLoanAmount() != null || creditDetails.getLoanTerm() != null || creditDetails.getInterestType() != null) {
            logger.info("Loan details changed, regenerating repayment schedule for credit ID: {}", id);
            // Clear the existing remboursements list in-memory
            credit.getRemboursements().clear();
            logger.info("Cleared existing remboursements for credit ID: {}", id);
            // Generate new repayment schedule
            List<Remboursement> repaymentSchedule = getRepaymentSchedule(credit);
            // Add new remboursements to the list
            credit.getRemboursements().addAll(repaymentSchedule);
            for (Remboursement remboursement : repaymentSchedule) {
                remboursement.setCredit(credit);
            }
            logger.info("Added {} new remboursements for credit ID: {}", repaymentSchedule.size(), id);
        }

        logger.info("Saving updated credit with ID: {}", id);
        Credit updatedCredit = creditRepository.save(credit);
        logger.info("Successfully updated credit with ID: {}", id);
        return updatedCredit;
    }
/*
    @Transactional
    public void deleteCredit(Long id) {
        logger.info("Deleting credit with ID: {}", id);
        Credit credit = getCreditById(id);
        logger.info("Fetched credit for deletion: {}", credit);
        logger.info("Deleting credit with ID: {} (cascading will handle remboursements)", id);
        creditRepository.delete(credit);
        logger.info("Successfully deleted credit with ID: {}", id);
    }*/


/*

    public void deleteCredit(Long id) {
        Credit credit = getCreditById(id);
        remboursementRepository.deleteByCredit(credit); // Delete associated remboursements
        creditRepository.delete(credit);
    }
*/

    @Transactional
    public void deleteCredit(Long id) {
        logger.info("Deleting credit with ID: {}", id);
        Credit credit = creditRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Credit not found with ID: {}", id);
                    return new RuntimeException("Credit not found with id: " + id);
                });
        remboursementRepository.deleteByCredit(credit);
        logger.info("Deleted associated remboursements for credit ID: {}", id);
        creditRepository.delete(credit);
        logger.info("Successfully deleted credit with ID: {}", id);
    }

    public List<Remboursement> getRepaymentSchedule(Credit credit) {
        logger.info("Generating repayment schedule for credit ID: {}", credit.getCreditId());
        if (credit.getLoanAmount() == null || credit.getLoanTerm() == null || credit.getInterestType() == null) {
            logger.error("Cannot generate repayment schedule: loanAmount={}, loanTerm={}, interestType={}",
                    credit.getLoanAmount(), credit.getLoanTerm(), credit.getInterestType());
            throw new IllegalStateException("Loan amount, loan term, and interest type must not be null");
        }

        Double loanAmount = credit.getLoanAmount();
        Integer loanTerm = credit.getLoanTerm();
        Double annualInterestRate = 0.05;
        Double monthlyInterestRate = annualInterestRate / 12.0;

        switch (credit.getInterestType()) {
            case BLOC:
                return generateRemboursementEnBlocSchedule(loanAmount, annualInterestRate, loanTerm);
            case CONSTANT:
                return generateAmortissementConstantSchedule(loanAmount, monthlyInterestRate, loanTerm);
            case NON_CONSTANT:
                return generateAmortissementNotConstantSchedule(loanAmount, monthlyInterestRate, loanTerm);
            default:
                throw new IllegalArgumentException("Invalid interest type: " + credit.getInterestType());
        }
    }

    private List<Remboursement> generateRemboursementEnBlocSchedule(Double loanAmount, Double annualInterestRate, Integer loanTerm) {
        List<Remboursement> remboursements = new ArrayList<>();
        Double totalRepayment = loanAmount * (1 + annualInterestRate * (loanTerm / 12.0));

        Remboursement remboursement = new Remboursement();
        remboursement.setMonth(loanTerm);
        remboursement.setAnnuite(totalRepayment);
        remboursement.setCapitalRepayment(loanAmount);
        remboursement.setInterest(totalRepayment - loanAmount);
        remboursement.setRemainingCapital(0.0);
        remboursements.add(remboursement);
        return remboursements;
    }

    private List<Remboursement> generateAmortissementConstantSchedule(Double loanAmount, Double monthlyInterestRate, Integer loanTerm) {
        List<Remboursement> remboursements = new ArrayList<>();
        Double monthlyPayment = loanAmount * (monthlyInterestRate * Math.pow(1 + monthlyInterestRate, loanTerm)) /
                (Math.pow(1 + monthlyInterestRate, loanTerm) - 1);
        Double remainingCapital = loanAmount;

        for (int i = 1; i <= loanTerm; i++) {
            Double interest = remainingCapital * monthlyInterestRate;
            Double capitalRepayment = monthlyPayment - interest;
            remainingCapital -= capitalRepayment;

            Remboursement remboursement = new Remboursement();
            remboursement.setMonth(i);
            remboursement.setAnnuite(monthlyPayment);
            remboursement.setCapitalRepayment(capitalRepayment);
            remboursement.setInterest(interest);
            remboursement.setRemainingCapital(remainingCapital);
            remboursements.add(remboursement);
        }
        return remboursements;
    }

    private List<Remboursement> generateAmortissementNotConstantSchedule(Double loanAmount, Double monthlyInterestRate, Integer loanTerm) {
        List<Remboursement> remboursements = new ArrayList<>();
        Double remainingCapital = loanAmount;
        Double equalPrincipalRepayment = loanAmount / loanTerm;

        for (int i = 1; i <= loanTerm; i++) {
            Double interest = remainingCapital * monthlyInterestRate;
            Double annuity = interest + equalPrincipalRepayment;
            remainingCapital -= equalPrincipalRepayment;

            Remboursement remboursement = new Remboursement();
            remboursement.setMonth(i);
            remboursement.setAnnuite(annuity);
            remboursement.setCapitalRepayment(equalPrincipalRepayment);
            remboursement.setInterest(interest);
            remboursement.setRemainingCapital(remainingCapital);
            remboursements.add(remboursement);
        }
        return remboursements;
    }


    public List<Credit> searchAndFilterCredits(
            Credit.Gender gender,
            Credit.Married married,
            Credit.Education education,
            Credit.SelfEmployed selfEmployed,
            Credit.CreditType creditType,
            Credit.CreditStatus creditStatus,
            Credit.InterestType interestType,
            Double minLoanAmount,
            Double maxLoanAmount,
            Integer minLoanTerm,
            Integer maxLoanTerm
    ) {
        List<Credit> credits = creditRepository.searchAndFilterCredits(
                gender,
                married,
                education,
                selfEmployed,
                creditType,
                creditStatus,
                interestType,
                minLoanAmount,
                maxLoanAmount,
                minLoanTerm,
                maxLoanTerm
        );

        for (Credit credit : credits) {
            credit.setRemboursements(remboursementRepository.findByCredit(credit));
        }

        return credits;
    }

    @Transactional(readOnly = true)
    public List<Credit> searchAndFilterCreditsByUser(
            Long userId,
            Credit.Gender gender,
            Credit.Married married,
            Credit.Education education,
            Credit.SelfEmployed selfEmployed,
            Credit.CreditType creditType,
            Credit.CreditStatus creditStatus,
            Credit.InterestType interestType,
            Double minLoanAmount,
            Double maxLoanAmount,
            Integer minLoanTerm,
            Integer maxLoanTerm
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new RuntimeException("User not found with id: " + userId);
                });

        List<Credit> credits = creditRepository.searchAndFilterCreditsByUser(
                user,
                gender,
                married,
                education,
                selfEmployed,
                creditType,
                creditStatus,
                interestType,
                minLoanAmount,
                maxLoanAmount,
                minLoanTerm,
                maxLoanTerm
        );

        for (Credit credit : credits) {
            credit.setRemboursements(remboursementRepository.findByCredit(credit));
        }

        return credits;
    }

    /**
     * Checks if a user has any active (non-CLOSED) credits
     * @param userId The ID of the user to check
     * @return true if the user has active credits, false otherwise
     */
    public boolean hasActiveCredits(Long userId) {
        logger.info("Checking if user ID {} has active credits", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new RuntimeException("User not found with id: " + userId);
                });

        // Find credits for this user that are not in CLOSED status
        List<Credit> activeCredits = creditRepository.findByUserAndCreditStatusNot(
                user, Credit.CreditStatus.CLOSED);

        boolean hasActive = !activeCredits.isEmpty();
        logger.info("User ID {} has active credits: {}", userId, hasActive);
        return hasActive;
    }


    /**
     * Calculates the maximum loan amount a client can borrow based on their income and requested loan term
     * Uses debt-to-income ratio to determine affordability
     *
     * @param monthlyIncome Client's monthly income
     * @param loanTermMonths Loan term in months
     * @return Maximum loan amount the client can borrow
     */
    public Double calculateMaxLoanAmount(Double monthlyIncome, Integer loanTermMonths) {
        logger.info("Calculating maximum loan amount for client with monthly income: {} and loan term: {} months",
                monthlyIncome, loanTermMonths);

        // Constants for loan calculation
        final Double MAX_DEBT_TO_INCOME_RATIO = 0.36; // Maximum 36% of income can go to debt payments
        final Double ESTIMATED_MONTHLY_INTEREST_RATE = 0.05 / 12.0; // Estimated annual interest rate of 5%

        // Calculate maximum monthly payment the client can afford
        Double existingMonthlyDebts = 0.0; // This could be retrieved from credit bureau or client declaration
        Double maxMonthlyPayment = monthlyIncome * MAX_DEBT_TO_INCOME_RATIO - existingMonthlyDebts;

        // Calculate maximum loan amount using the loan payment formula
        // M = P * r * (1 + r)^n / ((1 + r)^n - 1) where M is monthly payment, P is principal
        // Solving for P (principal/loan amount): P = M * ((1 + r)^n - 1) / (r * (1 + r)^n)
        Double numerator = maxMonthlyPayment * (Math.pow(1 + ESTIMATED_MONTHLY_INTEREST_RATE, loanTermMonths) - 1);
        Double denominator = ESTIMATED_MONTHLY_INTEREST_RATE * Math.pow(1 + ESTIMATED_MONTHLY_INTEREST_RATE, loanTermMonths);
        Double maxLoanAmount = numerator / denominator;

        // Apply additional restrictions based on loan term
        if (loanTermMonths > 60) { // For loans over 5 years, reduce max amount by 10%
            maxLoanAmount = maxLoanAmount * 0.9;
        }

        // Set a reasonable minimum and maximum regardless of calculations
        Double MIN_LOAN_AMOUNT = 1000.0;
        Double MAX_LOAN_AMOUNT = 1000000.0;

        maxLoanAmount = Math.max(Math.min(maxLoanAmount, MAX_LOAN_AMOUNT), MIN_LOAN_AMOUNT);

        logger.info("Calculated maximum loan amount: {}", maxLoanAmount);
        return maxLoanAmount;
    }
}