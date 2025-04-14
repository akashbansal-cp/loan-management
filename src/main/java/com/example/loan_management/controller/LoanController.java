package com.example.loan_management.controller;


import com.example.loan_management.enums.LoanStatus;
import com.example.loan_management.exception.LoanValidationException;
import com.example.loan_management.model.Loan;
import com.example.loan_management.pojo.ApplyLoanRequest;
import com.example.loan_management.pojo.LoanRepaymentRequest;
import com.example.loan_management.service.LoanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@RestController
public class LoanController {

    private final LoanService loanService;

    LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    // Endpoint to apply for a loan.
    @PostMapping("/apply")
    public ResponseEntity<Loan> applyForLoan(@RequestBody ApplyLoanRequest applyLoanRequest) throws LoanValidationException {
        List<Error> error = applyLoanRequest.validate();
        if (!CollectionUtils.isEmpty(error)) {
            throw new LoanValidationException(error.toString());
        }

        String customerName = applyLoanRequest.getCustomerName();
        BigDecimal principal = applyLoanRequest.getPrincipal();
        Loan loan = loanService.applyForLoan(customerName, principal);
        return ResponseEntity.ok(loan);
    }

    // Endpoint to view loan details.
    @GetMapping("/{loanId}")
    public ResponseEntity<Loan> getLoanDetails(@PathVariable String loanId) throws LoanValidationException {
        Optional<Loan> loanOpt = loanService.getLoanDetails(loanId);
        if (loanOpt.isEmpty()) {
            throw new LoanValidationException("Invalid LoanId");
        }
        return loanOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoint to make a repayment.
    @PostMapping("/{loanId}/repayments")
    public ResponseEntity<Loan> makeRepayment(@PathVariable String loanId, @RequestBody LoanRepaymentRequest request) throws LoanValidationException {
        List<Error> errors = request.validate();

        if (!CollectionUtils.isEmpty(errors)) {
            throw new LoanValidationException(errors.toString());
        }
        Optional<Loan> loanOpt = loanService.makeRepayment(loanId, request.getAmount());
        return loanOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoint to check loan status.
    @GetMapping("/{loanId}/status")
    public ResponseEntity<Map<String, String>> checkLoanStatus(@PathVariable String loanId) throws LoanValidationException {
        Optional<LoanStatus> statusOpt = loanService.checkLoanStatus(loanId);
        if (statusOpt.isEmpty()) {
            throw new LoanValidationException("Invalid LoanID");
        }
        return statusOpt.map(status -> ResponseEntity.ok(Map.of("status", status.toString())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
