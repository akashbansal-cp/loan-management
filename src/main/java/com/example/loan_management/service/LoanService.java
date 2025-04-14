package com.example.loan_management.service;

import com.example.loan_management.enums.LoanStatus;
import com.example.loan_management.exception.LoanValidationException;
import com.example.loan_management.model.Loan;
import com.example.loan_management.repository.LoanRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class LoanService {

    private final LoanRepository loanRepository;

    LoanService(@NonNull LoanRepository loanRepository){
        this.loanRepository = loanRepository;
    }

    // Customer applies for a loan.
    public Loan applyForLoan(String customerName, BigDecimal principal) {
        // LoanStatus is currently for all applied loans, we may call a service to validate whether loan is approved or rejected
        Loan loan = new Loan();
        loan.setCustomerName(customerName);
        loan.setPrincipal(principal);
        loan.setOutstandingAmount(principal);
        loan.setStatus(LoanStatus.APPROVED);
        return loanRepository.save(loan);
    }

    // Retrieves loan details by ID.
    public Optional<Loan> getLoanDetails(String loanId) {
        return loanRepository.findById(loanId);
    }

    // Processes repayment.
    public Optional<Loan> makeRepayment(String loanId, BigDecimal amount) throws LoanValidationException {
        Optional<Loan> loanOpt = loanRepository.findById(loanId);
        if (loanOpt.isPresent()) {
            Loan loan = loanOpt.get();
            if(LoanStatus.REPAID.compareTo(loan.getStatus()) == 0){
                throw new LoanValidationException("Loan is paid fully. No other actions are available");
            }
            if(LoanStatus.APPROVED.compareTo(loan.getStatus()) != 0){
                throw new LoanValidationException("Loan is not yet approved");
            }
            BigDecimal newOutstanding = loan.getOutstandingAmount().subtract(amount);
            if (newOutstanding.compareTo(BigDecimal.ZERO) <= 0) {
                loan.setOutstandingAmount(BigDecimal.ZERO);
                loan.setStatus(LoanStatus.REPAID);
            } else {
                loan.setOutstandingAmount(newOutstanding);
            }
            loanRepository.save(loan);
            return Optional.of(loan);
        }
        throw new LoanValidationException("No loan found for given LoanID");
    }

    // Returns the current status of the loan.
    public Optional<LoanStatus> checkLoanStatus(String loanId) {
        return loanRepository.findById(loanId).map(Loan::getStatus);
    }

}
