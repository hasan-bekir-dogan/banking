package com.banking;

import com.banking.model.*;
import com.banking.model.dto.AccountDetailDto;
import com.banking.model.dto.AccountDto;
import com.banking.model.dto.AccountRequestDto;
import com.banking.services.AccountService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class ModelTest {
    @Autowired
    private AccountService accountService;

    @Test
    public void bankAccountTest() throws Throwable {
        AccountDto account = accountService.createAccount(new AccountRequestDto("Jim", 0));

        Transaction transaction = new DepositTransaction(account.getAccountNumber(), 1000);
        AccountDetailDto accountDetailDto = accountService.getAccount(account.getAccountNumber());
        Account account2 = accountService.AccountDetailDtoToEntity(accountDetailDto);
        transaction.setAccountId(account2);
        String depositApprovalCode = accountService.post(transaction);

        transaction = new WithdrawalTransaction(account.getAccountNumber(), 200);
        accountDetailDto = accountService.getAccount(account.getAccountNumber());
        account2 = accountService.AccountDetailDtoToEntity(accountDetailDto);
        transaction.setAccountId(account2);
        String withdrawApprovalCode = accountService.post(transaction);

        transaction = new PhoneBillPaymentTransaction(account.getAccountNumber(), 96.50);
        accountDetailDto = accountService.getAccount(account.getAccountNumber());
        account2 = accountService.AccountDetailDtoToEntity(accountDetailDto);
        transaction.setAccountId(account2);
        String phoneBillApprovalCode = accountService.post(transaction);

        AccountDetailDto proceededAccount = accountService.getAccount(account.getAccountNumber());

        Assertions.assertEquals(proceededAccount.getBalance(), 703.50, 0.0001);
    }
}
