package com.example.loan_management.serviceTest;

import com.example.loan_management.enums.LoanStatus;
import com.example.loan_management.exception.LoanValidationException;
import com.example.loan_management.model.Loan;
import com.example.loan_management.repository.LoanRepository;
import com.example.loan_management.service.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanService loanService;

    private Loan validLoan;

    @BeforeEach
    public void setUp() {
        validLoan = new Loan();
        validLoan.setCustomerName("Alice");
        validLoan.setPrincipal(new BigDecimal("10000"));
        validLoan.setOutstandingAmount(new BigDecimal("10000"));
        validLoan.setStatus(LoanStatus.APPROVED);
    }

    @Test
    public void testApplyForLoan_Success() {
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Loan result = loanService.applyForLoan("Alice", new BigDecimal("10000"));
        ArgumentCaptor<Loan> loanCaptor = ArgumentCaptor.forClass(Loan.class);
        verify(loanRepository).save(loanCaptor.capture());
        Loan savedLoan = loanCaptor.getValue();
        assertEquals("Alice", savedLoan.getCustomerName());
        assertNotNull(result);
        assertEquals("Alice", result.getCustomerName());
        assertEquals(new BigDecimal("10000"), result.getPrincipal());
        assertEquals(new BigDecimal("10000"), result.getOutstandingAmount());
        assertEquals(LoanStatus.APPROVED, result.getStatus());


    }


    @Test
    public void testGetLoanDetails_Success() {
        when(loanRepository.findById("loan123")).thenReturn(Optional.of(validLoan));

        Optional<Loan> resultOpt = loanService.getLoanDetails("loan123");

        assertTrue(resultOpt.isPresent());
        Loan result = resultOpt.get();
        assertEquals("Alice", result.getCustomerName());
        assertEquals(new BigDecimal("10000"), result.getPrincipal());
    }

    @Test
    public void testGetLoanDetails_NotFound() {
        when(loanRepository.findById("invalid")).thenReturn(Optional.empty());

        Optional<Loan> resultOpt = loanService.getLoanDetails("invalid");

        assertFalse(resultOpt.isPresent());
    }


    @Test
    public void testMakeRepayment_PartialRepayment() throws LoanValidationException {
        when(loanRepository.findById("loan123")).thenReturn(Optional.of(validLoan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Loan> resultOpt = loanService.makeRepayment("loan123", new BigDecimal("4000"));

        assertTrue(resultOpt.isPresent());
        Loan result = resultOpt.get();
        assertEquals(new BigDecimal("6000"), result.getOutstandingAmount());
        assertEquals(LoanStatus.APPROVED, result.getStatus());
    }

    @Test
    public void testMakeRepayment_FullRepayment() throws LoanValidationException {
        when(loanRepository.findById("loan123")).thenReturn(Optional.of(validLoan));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Loan> resultOpt = loanService.makeRepayment("loan123", new BigDecimal("10000"));

        assertTrue(resultOpt.isPresent());
        Loan result = resultOpt.get();
        assertEquals(BigDecimal.ZERO, result.getOutstandingAmount());
        assertEquals(LoanStatus.REPAID, result.getStatus());
    }

    @Test
    public void testMakeRepayment_LoanAlreadyRepaid() {
        validLoan.setStatus(LoanStatus.REPAID);
        when(loanRepository.findById("loan123")).thenReturn(Optional.of(validLoan));

        assertThrows(LoanValidationException.class, () -> loanService.makeRepayment("loan123", new BigDecimal("500")), "Loan is paid fully. No other actions are available");
    }

    @Test
    public void testMakeRepayment_LoanNotApproved() {
        validLoan.setStatus(LoanStatus.PENDING); // For demonstration, REPAID is non-approved.
        when(loanRepository.findById("loan123")).thenReturn(Optional.of(validLoan));

        assertThrows(LoanValidationException.class, () ->
                loanService.makeRepayment("loan123", new BigDecimal("500")), "Loan is not yet approved"
        );
    }

    @Test
    public void testMakeRepayment_LoanNotFound() {
        when(loanRepository.findById("nonexistent")).thenReturn(Optional.empty());

        LoanValidationException exception = assertThrows(LoanValidationException.class, () ->
                loanService.makeRepayment("nonexistent", new BigDecimal("500"))
        );
        assertEquals("No loan found for given LoanID", exception.getMessage());
    }

    @Test
    public void testCheckLoanStatus_Success() {
        when(loanRepository.findById("loan123")).thenReturn(Optional.of(validLoan));

        Optional<com.example.loan_management.enums.LoanStatus> statusOpt = loanService.checkLoanStatus("loan123");

        assertTrue(statusOpt.isPresent());
        assertEquals(LoanStatus.APPROVED, statusOpt.get());
    }

    @Test
    public void testCheckLoanStatus_NotFound() {
        when(loanRepository.findById("nonexistent")).thenReturn(Optional.empty());

        Optional<LoanStatus> statusOpt = loanService.checkLoanStatus("nonexistent");

        assertFalse(statusOpt.isPresent());
    }

}
