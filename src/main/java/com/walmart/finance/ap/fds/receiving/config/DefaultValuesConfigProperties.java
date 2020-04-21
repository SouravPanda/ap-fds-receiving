package com.walmart.finance.ap.fds.receiving.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("default.value")
public class DefaultValuesConfigProperties {

    private String receivingControlNumber;

    private Double totalCostAmount;

    private Double totalRetailAmount;

    private Double bottleDepositAmount;

    private Integer departmentNumber;

    private Integer baseDivisionNumber;

    private Integer controlSequenceNumber;

    private String upcNumber;

    private Integer costMultiple;

    private Integer lineNumber;

    private String bottleDepositFlag;

    private String variableWeightIndicator;

    private Long itemNumber;

    private Integer quantity;

    private Integer receivedQuantity;

    private Double receivedWeightQuantity;

    private Integer lineCount;

    private String carrierCode;

    private String trailerNbr;

    private String billNbr;

    private Integer pymtStatCode;

    private Long freightId;





}