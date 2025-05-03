package com.example.BACK.service;

import com.example.BACK.model.Credit;
import com.example.BACK.model.Remboursement;
import com.example.BACK.model.User;
import com.twilio.twiml.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.BACK.repository.CreditRepository;



import com.example.BACK.model.User;
import com.example.BACK.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ReminderService {

    private static final Logger logger = LoggerFactory.getLogger(ReminderService.class);

    @Autowired
    private CreditRepository creditRepository;

    @Autowired
    private SmsService smsService;

    /**
     * Scheduled job to run daily at midnight to send payment reminders
     * Sends reminders 3 days before payment due date
     */
    @Scheduled(cron = "0 0 0 * * ?") // Run at midnight every day
    @Transactional(readOnly = true)
    public void sendPaymentReminders() {
        logger.info("Running scheduled payment reminder check");

        // Calculate the date for which we need to send reminders (3 days from now)
        LocalDate reminderDate = LocalDate.now().plusDays(3);
        int dayOfMonth = reminderDate.getDayOfMonth();

        // Get all active credits
        List<Credit> activeCredits = creditRepository.findByCreditStatus(Credit.CreditStatus.APPROVED);
        logger.info("Found {} active credits to check for reminders", activeCredits.size());

        int remindersSent = 0;

        for (Credit credit : activeCredits) {
            // Skip credits without users or phone numbers
            if (credit.getUser() == null || credit.getUser().getTel() == null) {
                logger.warn("Credit ID {} has no user or phone number, skipping reminder", credit.getCreditId());
                continue;
            }

            // Find the next payment due
            Remboursement nextPayment = findNextPayment(credit);

            // If a payment is due and it's on the reminderDate
            if (nextPayment != null && dayOfMonth == 1) { // Assuming payments are due on the 1st of each month
                // Send reminder SMS
                String phoneNumber = String.valueOf(credit.getUser().getTel());
                Double paymentAmount = nextPayment.getAnnuite();
                boolean sent = sendPaymentReminder(credit.getUser(), phoneNumber, paymentAmount, reminderDate);
                if (sent) {
                    remindersSent++;
                }
            }
        }

        logger.info("Payment reminder job completed. Sent {} reminders", remindersSent);
    }

    /**
     * Find the next payment due for a credit
     */
    private Remboursement findNextPayment(Credit credit) {
        // Get the current month of the credit lifecycle
        LocalDate startDate = credit.getLastPaymentDate() != null ?
                credit.getLastPaymentDate() :
                LocalDate.now().minusMonths(1); // Default to last month if no payments yet

        LocalDate now = LocalDate.now();
        long monthsElapsed = java.time.Period.between(startDate, now).toTotalMonths() + 1;

        // Find the corresponding repayment schedule entry
        if (credit.getRemboursements() != null && !credit.getRemboursements().isEmpty()) {
            List<Remboursement> sortedRemboursements = new ArrayList<>(credit.getRemboursements());
            sortedRemboursements.sort(Comparator.comparing(Remboursement::getMonth));

            for (Remboursement remboursement : sortedRemboursements) {
                if (remboursement.getMonth() == monthsElapsed) {
                    return remboursement;
                }
            }
        }

        return null;
    }

    /**
     * Send a payment reminder via SMS
     */
    private boolean sendPaymentReminder(User user, String phoneNumber, Double amount, LocalDate dueDate) {
        String message = String.format(
                "Hello %s, This is a reminder that your loan payment of %.2f TND is due on %s. " +
                        "Please ensure your account has sufficient funds. Contact us at +216-xx-xxx-xxx for assistance.",
                user.getPrenom(),
                amount,
                dueDate.toString()
        );

        logger.info("Sending payment reminder to {} at {}", user.getPrenom(), phoneNumber);
        return smsService.sendPaymentReminderSms(phoneNumber, user.getPrenom(), amount, dueDate);
    }

    /**
     * Manual method to send a reminder for a specific credit
     */
    @Transactional(readOnly = true)
    public boolean sendManualReminder(Long creditId) {
        logger.info("Sending manual payment reminder for credit ID: {}", creditId);

        Credit credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new RuntimeException("Credit not found with id: " + creditId));

        if (credit.getUser() == null || credit.getUser().getTel() == null) {
            throw new RuntimeException("Credit has no user or phone number");
        }

        // Find the next payment due
        Remboursement nextPayment = findNextPayment(credit);

        if (nextPayment == null) {
            throw new RuntimeException("No upcoming payments found for this credit");
        }

        // Calculate due date (assuming payment is due on the 1st of next month)
        LocalDate dueDate = LocalDate.now().plusMonths(1).withDayOfMonth(1);

        // Send reminder SMS
        String phoneNumber = String.valueOf(credit.getUser().getTel());
        Double paymentAmount = nextPayment.getAnnuite();
        return sendPaymentReminder(credit.getUser(), phoneNumber, paymentAmount, dueDate);
    }

    /**
     * Get all credits that have payments due in the next N days
     */
    @Transactional(readOnly = true)
    public List<Credit> getCreditsWithUpcomingPayments(int daysAhead) {
        List<Credit> result = new ArrayList<>();
        LocalDate targetDate = LocalDate.now().plusDays(daysAhead);
        int dayOfMonth = targetDate.getDayOfMonth();

        // Get all active credits
        List<Credit> activeCredits = creditRepository.findByCreditStatus(Credit.CreditStatus.APPROVED);

        for (Credit credit : activeCredits) {
            if (credit.getUser() != null && dayOfMonth == 1) { // Assuming payments are due on the 1st
                result.add(credit);
            }
        }

        return result;
    }

    @Transactional(readOnly = true)
    public boolean sendTestReminder(Long creditId, String testPhoneNumber) {
        logger.info("Sending test payment reminder for credit ID: {}", creditId);

        Credit credit = creditRepository.findById(creditId)
                .orElseThrow(() -> new RuntimeException("Credit not found with id: " + creditId));

        // Calculate due date (assuming payment is due on the 1st of next month)
        LocalDate dueDate = LocalDate.now().plusMonths(1).withDayOfMonth(1);

        // Use test phone number instead of user's number
        Double paymentAmount = 500.0; // Example amount
        return smsService.sendPaymentReminderSms(testPhoneNumber,
                credit.getUser().getPrenom(),
                paymentAmount,
                dueDate);
    }
}