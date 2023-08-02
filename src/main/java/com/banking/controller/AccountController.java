package com.banking.controller;

import com.banking.model.Account;
import com.banking.model.DepositTransaction;
import com.banking.model.Transaction;
import com.banking.model.WithdrawalTransaction;
import com.banking.model.dto.*;
import com.banking.model.exception.CheckAmountException;
import com.banking.services.AccountService;
import com.banking.services.TransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/account/v1")
@Api(value = "Account Api Documentation")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    Logger logger = LoggerFactory.getLogger(AccountController.class);

    // Save Account
    // http://localhost:8080/account/v1
    @ApiOperation(value = "Save Account")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "Saved account"),
                    @ApiResponse(code = 400, message = "Bad request"),
                    @ApiResponse(code = 500, message = "Something went wrong")
            })
    @PostMapping("")
    public ResponseEntity<Object> create(@RequestBody AccountRequestDto accountRequestDto) {
        try {
            AccountDto responseData = accountService.createAccount(accountRequestDto);

            logger.info("Saved account number that is: " + responseData.getAccountNumber());

            return new ResponseEntity<>(new AccountResponseDto("OK", responseData), HttpStatus.CREATED);
        } catch (Error e) {
            logger.error("Error when account saving: " + accountRequestDto.getOwner());

            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get Account Data
    // http://localhost:8080/account/v1/46897964341
    @ApiOperation(value = "Get Current Account")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Current Account"),
                    @ApiResponse(code = 400, message = "Bad request"),
                    @ApiResponse(code = 500, message = "Something went wrong")
            })
    @GetMapping("/{account_number}")
    public ResponseEntity<Object> getAccount(@PathVariable String account_number) {
        try {
            logger.info("Account number that is: " + account_number);

            CurrentAccountResponseDto responseObject = new CurrentAccountResponseDto();
            AccountDetailDto accountDetailDto = accountService.getAccount(account_number);
            responseObject.setAccountNumber(accountDetailDto.getAccountNumber());
            responseObject.setOwner(accountDetailDto.getOwner());
            responseObject.setBalance(accountDetailDto.getBalance());
            responseObject.setCreatedDate(accountDetailDto.getCreatedDate());
            responseObject.setTransactions(transactionService.getTransactions(accountDetailDto.getId()));

            return new ResponseEntity<>(responseObject, HttpStatus.OK);
        } catch (Error e) {
            logger.error("Error when account getting: " + account_number);

            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Credit Account
    // http://localhost:8080/account/v1/credit/46897964341
    @ApiOperation(value = "Credit Account")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Credit amount"),
                    @ApiResponse(code = 400, message = "Bad request"),
                    @ApiResponse(code = 500, message = "Something went wrong")
            })
    @PostMapping("/credit/{account_number}")
    public ResponseEntity<Object> credit(@PathVariable String account_number,
                                         @RequestBody AccountAmountDto accountAmountDto) throws Throwable {
        try {
            if(accountAmountDto.getAmount() <= 0)
                throw new CheckAmountException("Your amount must be greater than zero!");

            Transaction transaction = new DepositTransaction(account_number, accountAmountDto.getAmount());
            transaction.setAmount(accountAmountDto.getAmount());
            AccountDetailDto accountDetailDto = accountService.getAccount(account_number);
            Account account = accountService.AccountDetailDtoToEntity(accountDetailDto);
            transaction.setAccountId(account);
            String approvalCode = accountService.post(transaction);

            logger.info("Credit amount that is: " + accountAmountDto.getAmount());

            return new ResponseEntity<>(new TransactionResponseDto("OK", approvalCode), HttpStatus.OK);
        } catch (Error e) {
            logger.error("Error when account crediting: " + account_number);

            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Debit Account
    // http://localhost:8080/account/v1/debit/46897964341
    @ApiOperation(value = "Debit Account")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Debit amount"),
                    @ApiResponse(code = 400, message = "Bad request"),
                    @ApiResponse(code = 500, message = "Something went wrong")
            })
    @PostMapping("/debit/{account_number}")
    public ResponseEntity<Object> debit(@PathVariable String account_number,
                                         @RequestBody AccountAmountDto accountAmountDto) throws Throwable {
        try {
            if(accountAmountDto.getAmount() <= 0)
                throw new CheckAmountException("Your amount must be greater than zero!");;

            Transaction transaction = new WithdrawalTransaction(account_number, accountAmountDto.getAmount());
            AccountDetailDto accountDetailDto = accountService.getAccount(account_number);
            Account account = accountService.AccountDetailDtoToEntity(accountDetailDto);
            transaction.setAccountId(account);
            String approvalCode = accountService.post(transaction);

            logger.info("Debit amount that is: " + accountAmountDto.getAmount());

            return new ResponseEntity<>(new TransactionResponseDto("OK", approvalCode), HttpStatus.OK);
        } catch (Error e) {
            logger.error("Error when account debiting: " + account_number);

            return new ResponseEntity<>(e, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
