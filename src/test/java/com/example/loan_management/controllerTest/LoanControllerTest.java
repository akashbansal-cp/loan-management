package com.example.loan_management.controllerTest;

import com.example.loan_management.controller.LoanController;
import com.example.loan_management.enums.LoanStatus;
import com.example.loan_management.exception.LoanValidationException;
import com.example.loan_management.model.Loan;
import com.example.loan_management.pojo.ApplyLoanRequest;
import com.example.loan_management.pojo.LoanRepaymentRequest;
import com.example.loan_management.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanControllerTest {

    @InjectMocks
    LoanController loanController;

    @Mock
    LoanService loanService;

    private Loan validLoan;

    @BeforeEach
    public void setUp() {
        validLoan = new Loan();
        validLoan.setCustomerName("Alice");
        validLoan.setPrincipal(new BigDecimal("10000"));
    }

    @Test
    public void testApplyForLoan_Success() throws Exception {
        // Prepare a valid ApplyLoanRequest.
        ApplyLoanRequest request = new ApplyLoanRequest();
        request.setCustomerName("Alice");
        request.setPrincipal(new BigDecimal("10000"));

        // When the service is called, return the validLoan.
        when(loanService.applyForLoan(ArgumentMatchers.eq("Alice"), ArgumentMatchers.eq(new BigDecimal("10000"))))
                .thenReturn(validLoan);

        Loan loan = loanController.applyForLoan(request).getBody();
        Loan validLoanResponse = new Loan();
        validLoanResponse.setStatus(LoanStatus.APPROVED);
        validLoanResponse.setOutstandingAmount(new BigDecimal("10000"));
        validLoanResponse.setPrincipal(new BigDecimal("10000"));
        assertEquals(validLoanResponse.getOutstandingAmount(), loan.getPrincipal());
        assertEquals(LoanStatus.APPROVED, validLoanResponse.getStatus());
    }

    @Test
    public void testApplyForLoan_ValidationFailure() throws Exception {
        // Prepare an invalid ApplyLoanRequest.
        // For instance, assume that a principal of zero is not allowed.
        ApplyLoanRequest request = new ApplyLoanRequest();
        request.setCustomerName("Alice");
        request.setPrincipal(new BigDecimal(0));

        assertThrows(LoanValidationException.class, () -> loanController.applyForLoan(request));

    }


    @Test
    public void testGetLoanDetails_Success() throws Exception {
        // When the service returns a valid loan
        Loan loan = validLoan;
        loan.setStatus(LoanStatus.APPROVED);
        loan.setOutstandingAmount(new BigDecimal(10000));
        when(loanService.getLoanDetails("loan123")).thenReturn(Optional.of(loan));

        Loan loanResponse = loanController.getLoanDetails("loan123").getBody();
        assertNotNull(loanResponse);
        assertEquals(loan.getStatus(), loanResponse.getStatus());
        assertEquals(loan.getOutstandingAmount(), loanResponse.getOutstandingAmount());

    }

    @Test
    public void testGetLoanDetails_InvalidLoanId() throws Exception {
        // When the service returns empty, the controller should throw a LoanValidationException.
        when(loanService.getLoanDetails("invalidId")).thenReturn(Optional.empty());

        assertThrows(LoanValidationException.class, () -> {
            loanController.getLoanDetails("invalidId");
        }, "Invalid LoanId");


    }


    @Test
    public void testMakeRepayment_Success() throws Exception {
        // Prepare a valid LoanRepaymentRequest.
        LoanRepaymentRequest repaymentRequest = new LoanRepaymentRequest();
        repaymentRequest.setAmount(new BigDecimal("4000"));

        // Create an updated loan with reduced outstanding amount.
        Loan updatedLoan = new Loan();
        updatedLoan.setCustomerName("Alice");
        updatedLoan.setPrincipal(new BigDecimal("10000"));
        updatedLoan.setOutstandingAmount(new BigDecimal("6000"));
        updatedLoan.setStatus(LoanStatus.APPROVED);

        when(loanService.makeRepayment(
                ArgumentMatchers.eq("loan123"),
                ArgumentMatchers.eq(new BigDecimal("4000"))
        )).thenReturn(Optional.of(updatedLoan));

        Loan responseLoan = loanController.makeRepayment("loan123", repaymentRequest).getBody();
        assertNotNull(responseLoan);
        assertEquals(new BigDecimal("6000"), responseLoan.getOutstandingAmount());
    }

    @Test
    public void testMakeRepayment_ValidationFailure() {
        // Prepare an invalid LoanRepaymentRequest (e.g., negative amount).
        LoanRepaymentRequest repaymentRequest = new LoanRepaymentRequest();
        repaymentRequest.setAmount(new BigDecimal("-100"));

        assertThrows(LoanValidationException.class, () -> loanController.makeRepayment("loan123", repaymentRequest));
    }

    @Test
    public void testMakeRepayment_LoanNotFound() throws Exception {
        // Prepare a valid repayment request.
        LoanRepaymentRequest repaymentRequest = new LoanRepaymentRequest();
        repaymentRequest.setAmount(new BigDecimal("5000"));

        // Simulate service returning an empty Optional for a non-existing loan.
        when(loanService.makeRepayment(
                ArgumentMatchers.eq("loanNotFound"),
                ArgumentMatchers.eq(new BigDecimal("5000"))
        )).thenThrow(new LoanValidationException("No loan found for given LoanID"));

        assertThrows(LoanValidationException.class, () -> loanController.makeRepayment("loanNotFound", repaymentRequest), "No loan found for given LoanID");
    }


    @Test
    public void testCheckLoanStatus_Success() throws Exception {
        when(loanService.checkLoanStatus("loan123")).thenReturn(Optional.of(LoanStatus.APPROVED));

        var response = loanController.checkLoanStatus("loan123").getBody();
        assertNotNull(response);
        assertEquals(LoanStatus.APPROVED.toString(), response.get("status"));
    }

    @Test
    public void testCheckLoanStatus_InvalidLoanId() {
        when(loanService.checkLoanStatus("invalidId")).thenReturn(Optional.empty());
        assertThrows(LoanValidationException.class, () -> loanController.checkLoanStatus("invalidId"));
    }

}
