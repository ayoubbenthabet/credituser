package com.example.BACK.controller;







import com.example.BACK.model.Credit;
import com.example.BACK.model.Remboursement;

import java.util.List;

public class CreditResponse {

    private Credit credit;
    private List<Remboursement> repaymentSchedule;

    public CreditResponse() {}

    public CreditResponse(Credit credit, List<Remboursement> repaymentSchedule) {
        this.credit = credit;
        this.repaymentSchedule = repaymentSchedule;
    }

    // Getters and setters
    public Credit getCredit() {
        return credit;
    }

    public void setCredit(Credit credit) {
        this.credit = credit;
    }

    public List<Remboursement> getRepaymentSchedule() {
        return repaymentSchedule;
    }

    public void setRepaymentSchedule(List<Remboursement> repaymentSchedule) {
        this.repaymentSchedule = repaymentSchedule;
    }

    @Override
    public String toString() {
        return "CreditResponse{" +
                "credit=" + credit +
                ", repaymentSchedule=" + repaymentSchedule +
                '}';
    }
}