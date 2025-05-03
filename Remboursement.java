package com.example.BACK.model;



import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;


import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
/*
@Entity
public class Remboursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "credit_id")
    @JsonIgnore // Ignore this field during serialization
    private Credit credit;

    private Integer month;
    private Double annuite;
    private Double capitalRepayment;
    private Double interest;
    private Double remainingCapital;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Credit getCredit() { return credit; }
    public void setCredit(Credit credit) { this.credit = credit; }
    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }
    public Double getAnnuite() { return annuite; }
    public void setAnnuite(Double annuite) { this.annuite = annuite; }
    public Double getCapitalRepayment() { return capitalRepayment; }
    public void setCapitalRepayment(Double capitalRepayment) { this.capitalRepayment = capitalRepayment; }
    public Double getInterest() { return interest; }
    public void setInterest(Double interest) { this.interest = interest; }
    public Double getRemainingCapital() { return remainingCapital; }
    public void setRemainingCapital(Double remainingCapital) { this.remainingCapital = remainingCapital; }
}*/

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Remboursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "credit_id")
    @JsonIgnore
    private Credit credit;

    private Integer month;
    private Double annuite;
    private Double capitalRepayment;
    private Double interest;
    private Double remainingCapital;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Credit getCredit() { return credit; }
    public void setCredit(Credit credit) { this.credit = credit; }
    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }
    public Double getAnnuite() { return annuite; }
    public void setAnnuite(Double annuite) { this.annuite = annuite; }
    public Double getCapitalRepayment() { return capitalRepayment; }
    public void setCapitalRepayment(Double capitalRepayment) { this.capitalRepayment = capitalRepayment; }
    public Double getInterest() { return interest; }
    public void setInterest(Double interest) { this.interest = interest; }
    public Double getRemainingCapital() { return remainingCapital; }
    public void setRemainingCapital(Double remainingCapital) { this.remainingCapital = remainingCapital; }
}