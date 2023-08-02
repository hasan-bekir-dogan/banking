package com.banking.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Date;
import java.util.List;

// DTO: Data Transfer Object
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Log4j2
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CurrentAccountResponseDto {
    @JsonProperty("accountNumber")
    private String accountNumber;

    @JsonProperty("owner")
    private String owner;

    @JsonProperty("balance")
    private double balance;

    @JsonProperty("createDate")
    private Date createdDate;

    @JsonProperty("transactions")
    private List<TransactionDto> transactions;
}
