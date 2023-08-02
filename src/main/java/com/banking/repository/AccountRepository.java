package com.banking.repository;

import com.banking.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    @Query(value = "Select * From account Where account_number = :account_number", nativeQuery = true)
    Account findByAccountNumber(@Param("account_number") String account_number);

    Optional<Account> findById(Account account);
}
