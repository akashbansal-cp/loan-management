package com.example.loan_management.repository;

import com.example.loan_management.model.Loan;
import lombok.Data;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class LoanRepository {

    private final Map<String, Loan> loanStore = new HashMap<>();

    public Loan save(Loan loan) {
        loanStore.put(loan.getId(), loan);
        return loan;
    }

    public Optional<Loan> findById(String id) {
        return Optional.ofNullable(loanStore.get(id));
    }

    public Collection<Loan> findAll() {
        return loanStore.values();
    }
}
