package com.banking.services.impl;

import com.banking.model.Account;
import com.banking.model.Transaction;
import com.banking.model.dto.TransactionDto;
import com.banking.model.dto.TransactionSaveDto;
import com.banking.repository.TransactionRepository;
import com.banking.services.TransactionService;
import com.banking.util.NumberGenerator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<TransactionDto> getTransactions(Long accountId) {
        List<TransactionDto> transactionDtos = new ArrayList<>();
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);

        for (Transaction trans: transactions) {
            TransactionDto transactionDto = new TransactionDto();
            transactionDto.setAmount(trans.getAmount());
            transactionDto.setType(trans.getType());
            transactionDto.setDate(trans.getCreatedDate());
            transactionDto.setApprovalCode(trans.getApprovalCode());
            transactionDtos.add(transactionDto);
        }

        return transactionDtos;
    }

    @Override
    public String saveTransaction(TransactionSaveDto transactionSaveDto) {
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionSaveDto.getAmount());
        transaction.setType(transactionSaveDto.getType());
        transaction.setApprovalCode(NumberGenerator.generateApprovalCode());

        Account foreignKeyAccount = new Account();
        foreignKeyAccount.setId(transactionSaveDto.getAccountId());
        transaction.setAccountId(foreignKeyAccount);

        transactionRepository.save(transaction);

        return transaction.getApprovalCode();
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    // Model Mapper
    // Entity => DTO
    @Override
    public TransactionDto EntityToDto(Transaction transaction) {
        TransactionDto transactionDto = modelMapper.map(transaction, TransactionDto.class);
        return transactionDto;
    }

    // DTO => Entity
    @Override
    public Transaction DtoToEntity(TransactionDto transactionDto) {
        Transaction transaction = modelMapper.map(transactionDto, Transaction.class);
        return transaction;
    }

}
