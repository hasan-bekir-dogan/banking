package com.banking.services;

import com.banking.model.dto.*;
import com.banking.model.Account;
import com.banking.model.Transaction;

public interface AccountService {
    // CRUD
    public AccountDto createAccount(AccountRequestDto accountRequestDto);
    public String post(Transaction transaction) throws Throwable;
    public AccountDetailDto getAccount(String accountNumber);

    // Model Mapper
    public AccountDto EntityToDto(Account account);
    public AccountDetailDto EntityToAccountDetailDto(Account account);
    public Account DtoToEntity(AccountDto accountDto);
    public Account AccountDetailDtoToEntity(AccountDetailDto accountDetailDto);
}
