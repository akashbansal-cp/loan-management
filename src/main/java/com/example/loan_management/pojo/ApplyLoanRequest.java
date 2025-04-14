package com.example.loan_management.pojo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
public class ApplyLoanRequest {

    private String customerName;
    private BigDecimal principal;


    public List<Error> validate() {
        List<Error> error = new ArrayList<>();
        if (Objects.isNull(customerName)) {
            error.add(new Error("Invalid customer name"));
        }
        if (Objects.isNull(principal) || principal.compareTo(BigDecimal.ZERO) <= 0) {
            error.add(new Error("Invalid principle amount"));
        }
        return error;
    }


}
