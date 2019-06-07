package com.walmart.finance.ap.fds.receiving.validator;

import com.walmart.finance.ap.fds.receiving.exception.InvalidValueException;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
@Component
public class WriteSummaryValidator {

    private Integer controlNumberList[] = {0, 1, 2, 3};
    List<Integer> listOfControlNumbers = Arrays.asList(controlNumberList);
    private Integer transactionTypeList[] = {0, 1, 2, 3, 4, 5, 99};
    List<Integer> listOfTransactionType = Arrays.asList(transactionTypeList);
    private Integer mdseConditionCodeList[] = {0, 1, 2, 3, 4, 5};
    List<Integer> listOfMdseConditionCode = Arrays.asList(mdseConditionCodeList);

    public void validate(ReceivingSummaryRequest receiveSummaryRequest) {
/*
        if (!(listOfControlNumbers.contains(receiveSummaryRequest.getControlNumber()))) {
            throw new InvalidValueException("Incorrect controlNumber passed");
        }
        try {
            Integer.valueOf(receiveSummaryRequest.getReceiptNumbers());

        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid entry, please enter an integer for poReceiveId");
        }
        if (!(listOfTransactionType.contains(receiveSummaryRequest.getTransactionType()))) {
            throw new InvalidValueException("Incorrect transactionType passed");
        }*/
        //if(!(listOfMdseConditionCode.contains(receiveSummaryRequest.get)))

    }

}
