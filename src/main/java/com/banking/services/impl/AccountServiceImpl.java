package com.banking.services.impl;

import com.banking.model.Transaction;
import com.banking.model.dto.*;
import com.banking.model.Account;
import com.banking.model.exception.InsufficientBalanceException;
import com.banking.model.exception.ResourceNotFoundException;
import com.banking.repository.AccountRepository;
import com.banking.services.AccountService;
import com.banking.services.TransactionService;
import com.banking.util.NumberGenerator;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public AccountDto createAccount(AccountRequestDto accountRequestDto) {
        Account account = new Account();
        account.setOwner(accountRequestDto.getOwner());
        account.setBalance((accountRequestDto.getBalance()));

        String accountNumber = NumberGenerator.generateAccountNumber();
        account.setAccountNumber(accountNumber);

        accountRepository.save(account);

        return EntityToDto(account);
    }

    @Override
    public String post(Transaction transaction) throws Throwable {
        Account acc = transaction.getAccountId();
        Account account = (Account) accountRepository.findById(acc.getId()).orElseThrow(() -> new ResourceNotFoundException("Account not exist with id " + transaction.getAccountId()));
        double newBalance = 0;

        if(transaction.getType() == TransactionType.DepositTransaction)
            newBalance = account.getBalance() + transaction.getAmount();
        else if(transaction.getType() == TransactionType.WithdrawalTransaction)
            newBalance = account.getBalance() - transaction.getAmount();

        if(newBalance < 0)
            throw new InsufficientBalanceException("Your Balance is insufficient!");

        account.setBalance(newBalance);
        accountRepository.save(account);

        TransactionSaveDto transactionSaveDto = new TransactionSaveDto();
        transactionSaveDto.setAmount(transaction.getAmount());
        transactionSaveDto.setType(transaction.getType());
        transactionSaveDto.setAccountId(account.getId());

        return transactionService.saveTransaction(transactionSaveDto);
    }

    @Override
    public AccountDetailDto getAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber);

        return EntityToAccountDetailDto(account);
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    // Model Mapper
    // Entity => DTO
    @Override
    public AccountDto EntityToDto(Account account) {
        AccountDto accountDto =  modelMapper.map(account, AccountDto.class);
        return accountDto;
    }
    @Override
    public AccountDetailDto EntityToAccountDetailDto(Account account) {
        AccountDetailDto accountDetailDto =  modelMapper.map(account, AccountDetailDto.class);
        return accountDetailDto;
    }

    // DTO => Entity
    @Override
    public Account DtoToEntity(AccountDto accountDto) {
        Account account = modelMapper.map(accountDto, Account.class);
        return account;
    }
    @Override
    public Account AccountDetailDtoToEntity(AccountDetailDto accountDetailDto) {
        Account account = modelMapper.map(accountDetailDto, Account.class);
        return account;
    }
}
