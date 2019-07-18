package com.walmart.finance.ap.fds.receiving.controller;

import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import com.walmart.finance.ap.fds.receiving.service.ReceivingInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * This controller is to integrate financial transaction service.
 */
@RestController
@RequestMapping(value = "{countryCode}/receiving/info")
@Api(value = "RESTful APIs for Receiving Info ")
public class ReceivingInfoController {

    @Autowired
    ReceivingInfoService receivingInfoService;

    @GetMapping
    @ApiOperation(value = "API to receive information based on various parameters.")
    @ApiResponses(value = {@ApiResponse(code = 500, message = "Internal Server Error"), @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 204, message = "No content")})
    public ReceivingResponse getReceivingInfo(@PathVariable(value = "countryCode") String countryCode
            , @RequestParam(value = "invoiceId", required = false) String invoiceId
            , @RequestParam(value = "invoiceNumber", required = false) String invoiceNumber
            , @RequestParam(value = "purchaseOrderNumber", required = false) String purchaseOrderNumber
            , @RequestParam(value = "purchaseOrderId", required = false) String purchaseOrderId
            , @RequestParam(value = "receiptNumbers", required = false) List<String> receiptNumbers
            , @RequestParam(value = "transactionType", required = false) String transactionType
            , @RequestParam(value = "controlNumber", required = false) String controlNumber
            , @RequestParam(value = "locationNumber", required = false) String locationNumber
            , @RequestParam(value = "divisionNumber", required = false) String divisionNumber
            , @RequestParam(value = "vendorNumber", required = false) String vendorNumber
            , @RequestParam(value = "departmentNumber", required = false) String departmentNumber
            , @RequestParam(value = "itemNumbers", required = false) List<String> itemNumbers
            , @RequestParam(value = "upcNumbers", required = false) List<String> upcNumbers
            , @RequestParam(value = "receiptDateStart", required = false) String receiptDateStart
            , @RequestParam(value = "receiptDateEnd", required = false) String receiptDateEnd
            , @RequestParam(value = "lineNumberFlag", required = false, defaultValue = "N") String lineNumberFlag) {
        return receivingInfoService.getSevice(countryCode, invoiceId, invoiceNumber, purchaseOrderNumber, purchaseOrderId
                , receiptNumbers, transactionType, controlNumber, locationNumber, divisionNumber, vendorNumber, departmentNumber, itemNumbers, upcNumbers, receiptDateStart, receiptDateEnd, lineNumberFlag);
    }
    
}
