package com.banking;


import java.io.IOException;

import com.banking.model.Account;
import com.banking.model.dto.*;
import com.banking.model.exception.ResourceNotFoundException;
import com.banking.repository.AccountRepository;
import com.banking.util.NumberGenerator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@WebAppConfiguration
public class ControllerTest {

    private MockMvc mvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Before
    public void setUp() {
        this.mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    protected String mapToJson(Object obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(obj);
    }

    protected <T> T mapFromJson(String json, Class<T> clazz)
            throws JsonParseException, JsonMappingException, IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, clazz);
    }

    @Test
    public void createAccountTest() throws Exception {
        // given
        String uri = "/account/v1";
        AccountRequestDto account = new AccountRequestDto();
        account.setOwner("Jim");
        account.setBalance(10000);

        // when
        String inputJson = mapToJson(account);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                                 .contentType(MediaType.APPLICATION_JSON_VALUE)
                                 .content(inputJson)).andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.CREATED.value(), status);

        String content = mvcResult.getResponse().getContentAsString();
        AccountResponseDto response = objectMapper.readValue(content, AccountResponseDto.class);
        AccountDto accountDto = response.getAccountDto();

        assertEquals(account.getOwner(), accountDto.getOwner());
        assertEquals(account.getBalance(), accountDto.getBalance(), 0.01);
    }

    @Test
    public void getAccountTest() throws Exception {
        // create account
        Account account = new Account();
        account.setOwner("Jim");
        account.setBalance(3000);
        account.setAccountNumber(NumberGenerator.generateAccountNumber());
        accountRepository.save(account);

        // given
        String uri = "/account/v1/" + String.valueOf(account.getAccountNumber());

        // when
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri)
                                 .contentType(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.OK.value(), status);

        String content = mvcResult.getResponse().getContentAsString();
        CurrentAccountResponseDto response = objectMapper.readValue(content, CurrentAccountResponseDto.class);

        assertEquals(account.getOwner(), response.getOwner());
        assertEquals(account.getBalance(), response.getBalance(), 0.01);
        assertEquals(account.getAccountNumber(), response.getAccountNumber());
    }

    @Test
    public void creditAccountTest() throws Exception {
        // create account
        Account account = new Account();
        account.setOwner("Jim");
        account.setBalance(3);
        account.setAccountNumber(NumberGenerator.generateAccountNumber());
        accountRepository.save(account);

        // given
        String uri = "/account/v1/credit/" + String.valueOf(account.getAccountNumber());
        AccountAmountDto accountAmountDto = new AccountAmountDto();
        accountAmountDto.setAmount(1200);

        // when
        String inputJson = mapToJson(accountAmountDto);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                                 .contentType(MediaType.APPLICATION_JSON_VALUE)
                                 .content(inputJson)).andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.OK.value(), status);

        Account accountData = (Account) accountRepository.findById(account.getId()).orElseThrow(() -> new ResourceNotFoundException("Account not exist with id " + account.getId()));
        double expectedBalance = account.getBalance() + accountAmountDto.getAmount();
        assertEquals(expectedBalance, accountData.getBalance(), 0.01);
    }

    @Test
    public void debitAccountTest() throws Exception {
        // create account
        Account account = new Account();
        account.setOwner("Jim");
        account.setBalance(3000);
        account.setAccountNumber(NumberGenerator.generateAccountNumber());
        accountRepository.save(account);

        // given
        String uri = "/account/v1/debit/" + String.valueOf(account.getAccountNumber());
        AccountAmountDto accountAmountDto = new AccountAmountDto();
        accountAmountDto.setAmount(1200);

        // when
        String inputJson = mapToJson(accountAmountDto);
        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                                 .contentType(MediaType.APPLICATION_JSON_VALUE)
                                 .content(inputJson)).andReturn();

        // then
        int status = mvcResult.getResponse().getStatus();
        assertEquals(HttpStatus.OK.value(), status);

        Account accountData = (Account) accountRepository.findById(account.getId()).orElseThrow(() -> new ResourceNotFoundException("Account not exist with id " + account.getId()));
        double expectedBalance = account.getBalance() - accountAmountDto.getAmount();
        assertEquals(expectedBalance, accountData.getBalance(), 0.01);
    }
}
