package com.banking.model;

import com.banking.model.dto.TransactionType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Builder
@Log4j2

@Entity
@Table(name = "transaction")
public class Transaction extends Base implements Serializable {

    @Column(name = "amount", columnDefinition = "Decimal(10,2) default '0.00'")
    private double amount;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(name = "approval_code", nullable = false)
    private String approvalCode;

    @ManyToOne
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account accountId;

    public Transaction(double amount, TransactionType type, String approvalCode, Account accountId) {
        this.amount = amount;
        this.type = type;
        this.approvalCode = approvalCode;
        this.accountId = accountId;
    }
}
