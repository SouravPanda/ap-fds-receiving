package com.walmart.finance.ap.fds.receiving.model;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ReceiveExpense {
    @NotEmpty
    private Integer expenseTypeCode;
    @NotEmpty
    private Double actualExpenseValue;
    @NotEmpty
    private char businessStatusCode;

    private Integer dealID;
    @NotEmpty
    private char expenseValueFormat;
    @NotEmpty
    private int paymentFrequencyCode;
    @NotEmpty
    private int paymentMethodCode;
    @NotEmpty
    private Integer remitCompanyID;
}
