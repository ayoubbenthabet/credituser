package com.example.BACK.controller;

import com.example.BACK.service.ReminderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {
    private static final Logger logger = LoggerFactory.getLogger(ReminderController.class);

    @Autowired
    private ReminderService reminderService;

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
