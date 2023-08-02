package com.banking.model;

import com.banking.model.dto.TransactionType;

import lombok.Data;
import lombok.extern.log4j.Log4j2;

// DTO: Data Transfer Object
@Data
@Log4j2
public class PhoneBillPaymentTransaction extends Transaction{
    private String accountNumber;
    private double amount;
    private TransactionType type;

    public PhoneBillPaymentTransaction(String accountNumber, double amount) {
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.type = TransactionType.WithdrawalTransaction;
    }
}
