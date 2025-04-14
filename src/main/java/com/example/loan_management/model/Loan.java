package com.example.loan_management.model;

import com.example.loan_management.enums.LoanStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
public class Loan {
    private String id;
    private String customerName;
    private BigDecimal principal;
    private BigDecimal outstandingAmount;
    private LoanStatus status;
    private LocalDateTime createdDate;

    public Loan() {
        this.id = UUID.randomUUID().toString();
        this.createdDate = LocalDateTime.now();
    }


}
