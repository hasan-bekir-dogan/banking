package com.banking.services;

import com.banking.model.Transaction;
import com.banking.model.dto.TransactionDto;
import com.banking.model.dto.TransactionSaveDto;

import java.util.List;

public interface TransactionService {
    public List<TransactionDto> getTransactions(Long accountId);
    public String saveTransaction(TransactionSaveDto transactionSaveDto);

    // Model Mapper
    public TransactionDto EntityToDto(Transaction transaction);
    public Transaction DtoToEntity(TransactionDto transactionDto);
}
