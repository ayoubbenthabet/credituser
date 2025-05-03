package com.example.BACK.model;



import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
public class Credit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long creditId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties({"credits", "groups", "passwordResetToken", "passwordResetTokenExpiration", "resetCode"})
    private User user;


    @NotNull(message = "Gender is required")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @NotNull(message = "Married status is required")
    @Enumerated(EnumType.STRING)
    private Married married;

    @NotNull(message = "Education is required")
    @Enumerated(EnumType.STRING)
    private Education education;

    @NotNull(message = "Self-employed status is required")
    @Enumerated(EnumType.STRING)
    private SelfEmployed selfEmployed;

    @NotNull(message = "Income is required")
    private Double income;

    @NotNull(message = "Loan amount is required")
    private Double loanAmount;

    @NotNull(message = "Loan term is required")
    private Integer loanTerm;

    @NotNull(message = "Credit type is required")
    @Enumerated(EnumType.STRING)
    private CreditType creditType;

    @NotNull(message = "Credit status is required")
    @Enumerated(EnumType.STRING)
    private CreditStatus creditStatus;

    @NotNull(message = "Interest type is required")
    @Enumerated(EnumType.STRING)
    private InterestType interestType;

    @OneToMany(mappedBy = "credit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Remboursement> remboursements = new ArrayList<>();

    // New fields for payment tracking
    private LocalDate lastPaymentDate;
    private Integer missedPayments = 0;
    private Double originalInterestRate = 0.05; // 5% default rate
    private Double currentInterestRate = 0.05;  // Will increase with penalties
    private Boolean inGracePeriod = true;       // First month grace period

    public enum Gender { FEMALE, MALE }
    public enum Married { YES, NO }
    public enum Education { GRADUATE, NOT_GRADUATE }
    public enum SelfEmployed { YES, NO }
    public enum CreditType { BUSINESS, AGRICULTURE, HOUSE, SMALL_AMOUNT }
    public enum CreditStatus { PENDING, APPROVED, REJECTED, CLOSED }
    public enum InterestType { CONSTANT, NON_CONSTANT, BLOC }

    // Getters and Setters
    public Long getCreditId() { return creditId; }
    public void setCreditId(Long creditId) { this.creditId = creditId; }
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    public Married getMarried() { return married; }
    public void setMarried(Married married) { this.married = married; }
    public Education getEducation() { return education; }
    public void setEducation(Education education) { this.education = education; }
    public SelfEmployed getSelfEmployed() { return selfEmployed; }
    public void setSelfEmployed(SelfEmployed selfEmployed) { this.selfEmployed = selfEmployed; }
    public Double getIncome() { return income; }
    public void setIncome(Double income) { this.income = income; }
    public Double getLoanAmount() { return loanAmount; }
    public void setLoanAmount(Double loanAmount) { this.loanAmount = loanAmount; }
    public Integer getLoanTerm() { return loanTerm; }
    public void setLoanTerm(Integer loanTerm) { this.loanTerm = loanTerm; }
    public CreditType getCreditType() { return creditType; }
    public void setCreditType(CreditType creditType) { this.creditType = creditType; }
    public CreditStatus getCreditStatus() { return creditStatus; }
    public void setCreditStatus(CreditStatus creditStatus) { this.creditStatus = creditStatus; }
    public InterestType getInterestType() { return interestType; }
    public void setInterestType(InterestType interestType) { this.interestType = interestType; }
    public List<Remboursement> getRemboursements() { return remboursements; }
    public void setRemboursements(List<Remboursement> remboursements) { this.remboursements = remboursements; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public void setLastPaymentDate(LocalDate lastPaymentDate) { this.lastPaymentDate = lastPaymentDate; }

    public Integer getMissedPayments() { return missedPayments; }
    public void setMissedPayments(Integer missedPayments) { this.missedPayments = missedPayments; }

    public Double getOriginalInterestRate() { return originalInterestRate; }
    public void setOriginalInterestRate(Double originalInterestRate) { this.originalInterestRate = originalInterestRate; }

    public Double getCurrentInterestRate() { return currentInterestRate; }
    public void setCurrentInterestRate(Double currentInterestRate) { this.currentInterestRate = currentInterestRate; }

    public Boolean getInGracePeriod() { return inGracePeriod; }
    public void setInGracePeriod(Boolean inGracePeriod) { this.inGracePeriod = inGracePeriod; }
    public LocalDate getLastPaymentDate() { return lastPaymentDate; }

}