package com.walmart.store.receive.pojo;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@ToString
@NoArgsConstructor
public class ReceiveExpense {
    private Integer expenseTypeCode;
    private Double actualExpenseValue;
    private char businessStatusCode;
    private Integer dealID;
    private char expenseValueFormat;
    private int paymentFrequencyCode;
    private int paymentMethodCode;
    private Integer remitCompanyID;
}
