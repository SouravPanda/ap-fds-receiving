package com.walmart.finance.ap.fds.receiving.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class FoundationSupplier implements Serializable {

    private Integer supplierNumber;
    @Size(max = 30)
    private String supplierName;
    @Size(max = 30)
    private String supplierAddress;
    @Size(max = 16)
    private String supplierPhoneNumber;
    @Size(max = 16)
    private String supplierEmail;

    private String sapSupplierNumber;
    @Size(max = 30)
    private String sapSupplierName;
    private String countryCode;
}
