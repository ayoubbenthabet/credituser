package com.example.BACK.controller;




import com.example.BACK.model.Credit;
import com.example.BACK.service.CreditService;
import com.example.BACK.service.PaymentPenaltyService;
import com.example.BACK.service.ReminderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.*;

import java.util.*;
/*
@RestController
@RequestMapping("/api/credits")
public class CreditController {

    @Autowired
    private CreditService creditService;

    @PostMapping
    public ResponseEntity<Credit> createCredit(@RequestBody Credit credit) {
        Credit createdCredit = creditService.createCredit(credit);
        return ResponseEntity.ok(createdCredit);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Credit> getCreditById(@PathVariable Long id) {
        Credit credit = creditService.getCreditById(id);
        return ResponseEntity.ok(credit);
    }

    @GetMapping
    public ResponseEntity<List<Credit>> getAllCredits() {
        List<Credit> credits = creditService.getAllCredits();
        return ResponseEntity.ok(credits);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCredit(@PathVariable Long id, @RequestBody Credit creditDetails) {
        try {
            Credit updatedCredit = creditService.updateCredit(id, creditDetails);
            return ResponseEntity.ok(updatedCredit);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error updating credit: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCredit(@PathVariable Long id) {
        creditService.deleteCredit(id);
        return ResponseEntity.noContent().build();
    }
}*/

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/*
@RestController
@RequestMapping("/api/credits")
public class CreditController {

    private static final Logger logger = LoggerFactory.getLogger(CreditController.class);

    @Autowired
    private CreditService creditService;

    @PostMapping
    public ResponseEntity<?> createCredit(@Valid @RequestBody Credit credit) {
        try {
            logger.info("Received POST request to create credit: {}", credit);
            Credit createdCredit = creditService.createCredit(credit);
            return ResponseEntity.ok(createdCredit);
        } catch (Exception e) {
            logger.error("Error creating credit: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error creating credit: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCreditById(@PathVariable Long id) {
        try {
            logger.info("Received GET request for credit ID: {}", id);
            Credit credit = creditService.getCreditById(id);
            return ResponseEntity.ok(credit);
        } catch (Exception e) {
            logger.error("Error fetching credit with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body("Error fetching credit: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllCredits() {
        try {
            logger.info("Received GET request for all credits");
            List<Credit> credits = creditService.getAllCredits();
            return ResponseEntity.ok(credits);
        } catch (Exception e) {
            logger.error("Error fetching all credits: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error fetching all credits: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCredit(@PathVariable Long id, @Valid @RequestBody Credit creditDetails) {
        try {
            logger.info("Received PUT request to update credit ID: {} with details: {}", id, creditDetails);
            Credit updatedCredit = creditService.updateCredit(id, creditDetails);
            return ResponseEntity.ok(updatedCredit);
        } catch (Exception e) {
            logger.error("Error updating credit with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body("Error updating credit: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCredit(@PathVariable Long id) {
        try {
            logger.info("Received DELETE request for credit ID: {}", id);
            creditService.deleteCredit(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting credit with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body("Error deleting credit: " + e.getMessage());
        }
    }
}*/

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/credits")
public class CreditController {

    private static final Logger logger = LoggerFactory.getLogger(CreditController.class);

    @Autowired
    private CreditService creditService;
    @Autowired
    private PaymentPenaltyService paymentPenaltyService;
    @Autowired
    private ReminderService reminderService;

    @PostMapping
    public ResponseEntity<?> createCredit(@Valid @RequestBody Credit credit) {
        try {
            logger.info("Received POST request to create credit: {}", credit);
            Credit createdCredit = creditService.createCredit(credit);
            return ResponseEntity.ok(createdCredit);
        } catch (Exception e) {
            logger.error("Error creating credit: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error creating credit: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCreditById(@PathVariable Long id) {
        try {
            logger.info("Received GET request for credit ID: {}", id);
            Credit credit = creditService.getCreditByIdWithRemboursements(id);
            return ResponseEntity.ok(credit);
        } catch (Exception e) {
            logger.error("Error fetching credit with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body("Error fetching credit: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllCredits() {
        try {
            logger.info("Received GET request for all credits");
            List<Credit> credits = creditService.getAllCredits();
            return ResponseEntity.ok(credits);
        } catch (Exception e) {
            logger.error("Error fetching all credits: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("Error fetching all credits: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCredit(@PathVariable Long id, @Valid @RequestBody Credit creditDetails) {
        try {
            logger.info("Received PUT request to update credit ID: {} with details: {}", id, creditDetails);
            Credit updatedCredit = creditService.updateCredit(id, creditDetails);
            return ResponseEntity.ok(updatedCredit);
        } catch (Exception e) {
            logger.error("Error updating credit with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(500).body("Error updating credit: " + e.getMessage());
        }
    }
    /*
        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteCredit(@PathVariable Long id) {
            creditService.deleteCredit(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }*/
    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCredit(@PathVariable Long id) {
        try {
            logger.info("Received DELETE request for credit ID: {}", id);
            creditService.deleteCredit(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting credit with ID {}: {}", id, e.getMessage(), e);
            if (e.getMessage().contains("Credit not found")) {
                return ResponseEntity.status(404).body("Credit not found with id: " + id);
            }
            return ResponseEntity.status(500).body("Error deleting credit: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<CreditResponse>> searchAndFilterCredits(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Credit.Gender gender,
            @RequestParam(required = false) Credit.Married married,
            @RequestParam(required = false) Credit.Education education,
            @RequestParam(required = false) Credit.SelfEmployed selfEmployed,
            @RequestParam(required = false) Credit.CreditType creditType,
            @RequestParam(required = false) Credit.CreditStatus creditStatus,
            @RequestParam(required = false) Credit.InterestType interestType,
            @RequestParam(required = false) Double minLoanAmount,
            @RequestParam(required = false) Double maxLoanAmount,
            @RequestParam(required = false) Integer minLoanTerm,
            @RequestParam(required = false) Integer maxLoanTerm
    ) {
        try {
            List<Credit> credits;


            if (userId != null) {
                credits = creditService.searchAndFilterCreditsByUser(
                        userId,
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
            } else {
                credits = creditService.searchAndFilterCredits(
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
            }

            List<CreditResponse> responses = new ArrayList<>();
            /*for (Credit credit : credits) {
                List<Remboursement> repaymentSchedule = creditService.getRepaymentSchedule(credit);
                CreditResponse response = new CreditResponse(credit, repaymentSchedule);
                responses.add(response);
            }*/

            return new ResponseEntity<>(responses, HttpStatus.OK);
        } catch (Exception e) {
            // return ResponseEntity.status(500).body("Failed to search and filter credits: " + e.getMessage());
            return null;
        }
    }


    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createCreditForUser(@PathVariable Long userId, @Valid @RequestBody Credit credit) {
        try {
            logger.info("Received POST request to create credit for user ID {}: {}", userId, credit);
            Credit createdCredit = creditService.createCredit(credit, userId);
            return ResponseEntity.ok(createdCredit);
        } catch (Exception e) {
            logger.error("Error creating credit for user ID {}: {}", userId, e.getMessage(), e);
            // If the error is because user already has active credits, return 400 Bad Request
            if (e.getMessage().contains("finish your existing credit")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("You already have an active credit. Please finish your existing credit before applying for a new one.");
            }
            return ResponseEntity.status(500).body("Error creating credit: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getCreditsByUserId(@PathVariable Long userId) {
        try {
            logger.info("Received GET request for credits by user ID: {}", userId);
            List<Credit> credits = creditService.getCreditsByUserId(userId);
            return ResponseEntity.ok(credits);
        } catch (Exception e) {
            logger.error("Error fetching credits for user ID {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(500).body("Error fetching credits: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}/can-apply")
    public ResponseEntity<Map<String, Object>> canUserApplyForCredit(@PathVariable Long userId) {
        try {
            boolean canApply = !creditService.hasActiveCredits(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("canApply", canApply);
            if (!canApply) {
                response.put("message", "You already have an active credit. Please finish your existing credit before applying for a new one.");
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error checking if user ID {} can apply for credit: {}", userId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error checking credit eligibility: " + e.getMessage()));
        }
    }

    @PostMapping("/{creditId}/payment")
    public ResponseEntity<?> recordPayment(
            @PathVariable Long creditId,
            @RequestParam Double amount) {
        try {
            logger.info("Received request to record payment of {} for credit ID: {}", amount, creditId);
            Credit updatedCredit = paymentPenaltyService.recordPayment(creditId, amount);
            return ResponseEntity.ok(updatedCredit);
        } catch (Exception e) {
            logger.error("Error recording payment for credit ID {}: {}", creditId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error recording payment: " + e.getMessage());
        }
    }

    @PostMapping("/{creditId}/check-penalty")
    public ResponseEntity<?> checkAndApplyPenalty(@PathVariable Long creditId) {
        try {
            logger.info("Received request to check penalties for credit ID: {}", creditId);
            Credit updatedCredit = paymentPenaltyService.checkAndApplyPenalty(creditId);
            return ResponseEntity.ok(updatedCredit);
        } catch (Exception e) {
            logger.error("Error checking penalties for credit ID {}: {}", creditId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error checking penalties: " + e.getMessage());
        }
    }

    @GetMapping("/max-loan-amount")
    public ResponseEntity<?> calculateMaxLoanAmount(
            @RequestParam Double monthlyIncome,
            @RequestParam Integer loanTermMonths) {
        try {
            Double maxAmount = creditService.calculateMaxLoanAmount(monthlyIncome, loanTermMonths);
            Map<String, Object> response = new HashMap<>();
            response.put("maxLoanAmount", maxAmount);
            response.put("monthlyIncome", monthlyIncome);
            response.put("loanTermMonths", loanTermMonths);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error calculating maximum loan amount: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating maximum loan amount: " + e.getMessage());
        }
    }

    @PostMapping("/{creditId}/send-reminder")
    public ResponseEntity<?> sendPaymentReminder(@PathVariable Long creditId) {
        try {
            logger.info("Received request to send payment reminder for credit ID: {}", creditId);
            reminderService.sendManualReminder(creditId);
            return ResponseEntity.ok("Payment reminder sent successfully");
        } catch (Exception e) {
            logger.error("Error sending payment reminder for credit ID {}: {}", creditId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending payment reminder: " + e.getMessage());
        }
    }

    @PostMapping("/test/{creditId}/{phoneNumber}")
    public ResponseEntity<?> sendTestReminder(@PathVariable Long creditId,
                                              @PathVariable String phoneNumber) {
        try {
            logger.info("Received request to send test payment reminder to {} for credit ID: {}",
                    phoneNumber, creditId);
            boolean sent = reminderService.sendTestReminder(creditId, phoneNumber);

            if (sent) {
                return ResponseEntity.ok("Test payment reminder sent successfully to " + phoneNumber);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to send test payment reminder");
            }
        } catch (Exception e) {
            logger.error("Error sending test payment reminder: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error sending test payment reminder: " + e.getMessage());
        }
    }

}