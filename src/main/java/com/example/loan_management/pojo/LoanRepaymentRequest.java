package com.example.loan_management.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class LoanRepaymentRequest {

    private BigDecimal amount;

    public List<Error> validate(){
        List<Error> errors = new ArrayList<>();
        if(Objects.isNull(amount) || amount.compareTo(BigDecimal.ZERO) <= 0){
            errors.add(new Error("Invalid amount received"));
        }
        return errors;
    }

}
