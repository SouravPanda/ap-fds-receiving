package com.walmart.finance.ap.fds.receiving.integrations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class InvoiceResponseData {


    @JsonProperty("invoiceId")
    private String invoiceId;

    @JsonProperty("invoiceNumber")
    private String invoiceNumber;

    @JsonProperty("poID")
    private String purchaseOrderId;

//    @JsonProperty("poNum")
    private String purchaseOrderNumber;

    @JsonProperty("receivingNum")
    private String receivingNum;

    @JsonProperty("destDivNbr")
    private String destDivNbr;

    @JsonProperty("destStoreNbr")
    private String destStoreNbr;

    @JsonProperty("vendorNumber")
    private String vendorNumber;

    @JsonProperty("invoiceDeptNumber")
    private String invoiceDeptNumber;

    @JsonProperty("invoiceReference")
    private List<InvoiceReferenceResponse> invoiceReferenceResponseList;



    /*


    @ApiModelProperty(required = true, notes = "Username")
    private String userName;

    @ApiModelProperty(notes = "Transaction Source Code for the request.", example = "IVE")
    private String transactionSrc;

    @ApiModelProperty(hidden = true)
    private Character invoiceType;

    @ApiModelProperty(hidden = true)
    private Character storeType;

    @ApiModelProperty(required = true, notes = "Please enter a valid country code like CR, SV etc")
    private String countryCode;

    @ApiModelProperty(required = true, notes = "It is the sequentially assigned systematically when scanning invoice")
    private String scanNo;

    @ApiModelProperty(required = true, notes = "PO Number should match the PO Number present on the Purchase Order")
    private Long purchaseOrderNo;

    @ApiModelProperty(required = true, notes = "Vendor Number on the PO should match with the vendor number on the Invoice.")
    private Long vendorNo;

    @ApiModelProperty(notes = "Batch Number")
    private String batchNo;

    @ApiModelProperty(hidden = true)
    private Boolean vendorDeptComboValid;

    @ApiModelProperty(hidden = true)
    private String termDesc;

    @ApiModelProperty(hidden = true)
    private Long termDiscountDays;

    @ApiModelProperty(hidden = true)
    private Long termNetDays;

    @ApiModelProperty(hidden = true)
    private Long generatedInvoiceId;

    @ApiModelProperty(notes = "Company Id. This is mandatory for countries like Hodurus")
    private String caiNumber;

    @ApiModelProperty(required = true, notes = "Mandatory field and store number should be valid field. Validate store on DIV/ Store combination for given country.")
    private Long storeNo;

    @ApiModelProperty(required = true, notes = "14 characters as per the new requirements for E-invoicing as we need to be aligned on same validation for e-invoicing, APIE, OCR process")
    private String serialNo;

    @ApiModelProperty(hidden = true)
    private Long divisionNo;

    @ApiModelProperty(required = true, notes = "The three-digit Department number is the two-digit department plus the one-digit sequence number.")
    private String departmentNo;

    @ApiModelProperty(required = true, notes = "Should be the invoice number on vendor invoice number. Invoice number must not be more than 15 digits. Will keep same as existing it allow alphanumeric characters.")
    private String invoiceNo;

    @ApiModelProperty(required = true, example = "26051990", notes = "Invoice date must not be greater than current date and should be in ‘DD/MM/YY’ format.")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate invoiceDate;

    @ApiModelProperty(notes = "Invoice cost must be greater than Zero for Invoices")
    private Double invoiceCost;

    @ApiModelProperty(notes = "Credit Cost must be less than Zero for Credit Memos. Should only have credit amount (-ve)")
    private Double creditCost;


    @ApiModelProperty(example = "26051990", notes = "Claim date should be post invoice date. This is valid only for Credit Memos.")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate claimDate;

    @ApiModelProperty(hidden = true, notes = "Discount amount should be calculated based on cost and vendor terms.")
    private Long discountAmount;

    @ApiModelProperty(hidden = true, notes = "Discount % is pulled from vendor terms.")
    private Integer discountPercent;

    @ApiModelProperty(notes = "Currency code need to be pulled from vendor Master based on Vendor terms.")
    private String currencyCode;

    @ApiModelProperty(notes = "Must be non-zero numeric and should be less than ‘1000’. Applicable only for Import Invoices")
    private Double exchangeRate;

    @ApiModelProperty(required = true, example = "26051990", notes = "Validation should be less than or equal to current date. Format is DD/MM/YY. It’s mandatory field for HN,NI,EL Salvador.")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate receptionDate;*/

}
