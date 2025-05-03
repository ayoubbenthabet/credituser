package com.example.BACK.service;

import com.example.BACK.model.Credit;
import com.example.BACK.model.Remboursement;
import com.example.BACK.repository.CreditRepository;
import com.example.BACK.repository.RemboursementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
@Service
public class PaymentPenaltyService implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(PaymentPenaltyService.class);
    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    @Autowired
    private CreditRepository creditRepository;
    @Autowired
    private RemboursementRepository remboursementRepository;

    // Constants for penalty calculation
    private static final Double PENALTY_RATE_INCREASE = 0.02; // 2% increase per missed payment
    private static final Double MAX_PENALTY_RATE = 0.15;      // Maximum 15% interest rate

    /**
     * Scheduled job to run at midnight on the 1st day of each month
     * This checks for missed payments and applies penalties
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    public void checkPaymentsAndApplyPenalties() {
        logger.info("Running scheduled payment check and penalty application");
        LocalDate today = LocalDate.now();
        LocalDate oneMonthAgo = today.minusMonths(1);

        // Get all active credits
        List<Credit> activeCredits = creditRepository.findByCreditStatus(Credit.CreditStatus.APPROVED);

        for (Credit credit : activeCredits) {
            // Check if payment is missing for the previous month
            if (credit.getLastPaymentDate() == null || credit.getLastPaymentDate().isBefore(oneMonthAgo)) {
                // Missed payment detected
                applyPenaltyToCredit(credit);
            }
        }
    }

    /**
     * Apply penalties to a credit for missed payments
     */
    @Transactional
    public void applyPenaltyToCredit(Credit credit) {
        // Update missed payments count
        credit.setMissedPayments(credit.getMissedPayments() + 1);

        // If in grace period (first month), just mark it as ended
        if (credit.getInGracePeriod()) {
            logger.info("Grace period ended for credit ID: {}", credit.getCreditId());
            credit.setInGracePeriod(false);
        } else {
            // Apply penalty by increasing the interest rate
            Double newRate = credit.getCurrentInterestRate() + PENALTY_RATE_INCREASE;

            // Cap at maximum penalty rate
            if (newRate > MAX_PENALTY_RATE) {
                newRate = MAX_PENALTY_RATE;
            }

            logger.info("Applying penalty to credit ID: {}. Interest rate increased from {}% to {}%",
                    credit.getCreditId(),
                    credit.getCurrentInterestRate() * 100,
                    newRate * 100);

            credit.setCurrentInterestRate(newRate);

            // Recalculate repayment schedule with the new interest rate
            regenerateRepaymentSchedule(credit);
        }

        // Save the updated credit
        creditRepository.save(credit);
    }

    /**
     * Manually check a specific credit for missed payments and apply penalties if needed
     */
    @Transactional
    public Credit checkAndApplyPenalty(Long creditId) {
        Credit credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new RuntimeException("Credit not found with id: " + creditId));

        LocalDate oneMonthAgo = LocalDate.now().minusMonths(1);

        // Check if payment is missing
        if (credit.getLastPaymentDate() == null || credit.getLastPaymentDate().isBefore(oneMonthAgo)) {
            applyPenaltyToCredit(credit);
        }

        return credit;
    }

    /**
     * Record a payment for a credit
     */
    @Transactional
    public Credit recordPayment(Long creditId, Double amount) {
        Credit credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new RuntimeException("Credit not found with id: " + creditId));

        // Update the last payment date to today
        credit.setLastPaymentDate(LocalDate.now());

        // You could add more logic here to track payment amounts, etc.

        logger.info("Payment of {} recorded for credit ID: {}", amount, creditId);

        return creditRepository.save(credit);
    }

    /**
     * Regenerate the repayment schedule with the new interest rate
     */
    private void regenerateRepaymentSchedule(Credit credit) {
        // Get a reference to your existing services
        CreditService creditService = applicationContext.getBean(CreditService.class);
        RemboursementRepository remboursementRepository = applicationContext.getBean(RemboursementRepository.class);

        // Clear existing repayment schedule
        remboursementRepository.deleteByCredit(credit);
        credit.getRemboursements().clear();

        // Calculate remaining loan amount and term
        Double remainingLoanAmount = calculateRemainingLoanAmount(credit);
        Integer remainingTerm = calculateRemainingTerm(credit);

        // Temporarily adjust the credit object for calculation
        Double originalLoanAmount = credit.getLoanAmount();
        Integer originalTerm = credit.getLoanTerm();

        credit.setLoanAmount(remainingLoanAmount);
        credit.setLoanTerm(remainingTerm);

        // Generate new repayment schedule with the current (penalized) interest rate
        List<Remboursement> newSchedule;
        switch (credit.getInterestType()) {
            case BLOC:
                newSchedule = generateRemboursementEnBlocSchedule(
                        remainingLoanAmount,
                        credit.getCurrentInterestRate(),
                        remainingTerm
                );
                break;
            case CONSTANT:
                newSchedule = generateAmortissementConstantSchedule(
                        remainingLoanAmount,
                        credit.getCurrentInterestRate() / 12.0,
                        remainingTerm
                );
                break;
            case NON_CONSTANT:
                newSchedule = generateAmortissementNotConstantSchedule(
                        remainingLoanAmount,
                        credit.getCurrentInterestRate() / 12.0,
                        remainingTerm
                );
                break;
            default:
                throw new IllegalArgumentException("Invalid interest type: " + credit.getInterestType());
        }

        // Restore original values
        credit.setLoanAmount(originalLoanAmount);
        credit.setLoanTerm(originalTerm);

        // Save new repayment schedule
        for (Remboursement remboursement : newSchedule) {
            remboursement.setCredit(credit);
            remboursementRepository.save(remboursement);
        }

        logger.info("Regenerated repayment schedule for credit ID: {} with new rate: {}%",
                credit.getCreditId(),
                credit.getCurrentInterestRate() * 100);
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


    private Double calculateRemainingLoanAmount(Credit credit) {
        // This should be implemented based on your business logic
        // For simplicity, let's assume the remaining capital in the last payment entry
        if (credit.getRemboursements() != null && !credit.getRemboursements().isEmpty()) {
            List<Remboursement> remboursements = new ArrayList<>(credit.getRemboursements());
            remboursements.sort(Comparator.comparing(Remboursement::getMonth));
            return remboursements.get(remboursements.size() - 1).getRemainingCapital();
        }

        // If no repayment entries, return the original loan amount
        return credit.getLoanAmount();
    }

    private Integer calculateRemainingTerm(Credit credit) {
        // Calculate remaining term based on how many payments have been made
        if (credit.getRemboursements() != null && !credit.getRemboursements().isEmpty()) {
            List<Remboursement> remboursements = new ArrayList<>(credit.getRemboursements());
            remboursements.sort(Comparator.comparing(Remboursement::getMonth));
            Integer lastPaidMonth = remboursements.get(remboursements.size() - 1).getMonth();
            return credit.getLoanTerm() - lastPaidMonth;
        }

        // If no repayment entries, return the original term
        return credit.getLoanTerm();
    }
}

