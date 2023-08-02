package com.banking.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

// DTO: Data Transfer Object
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Log4j2
public class AccountDto {
    private String accountNumber;

    @NotEmpty(message = "Owner must have value")
    private String owner;

    private double balance;
    private Date createdDate;
}
