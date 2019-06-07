package com.walmart.finance.ap.fds.receiving.controller;

import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.response.ReceivingSummaryResponse;
import com.walmart.finance.ap.fds.receiving.service.ReceiveSummaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "{countryCode}/receiving/summary")
@Api(value = "REST APIs for receiving-summary ")
public class ReceivingSummaryController {

    @Autowired
    private ReceiveSummaryService receiveSummaryService;

    /**
     * Method calls Service class to add stores in Db
     *
     * @param
     * @return
     */
    @PostMapping
    @ApiOperation(value = "API to add new Stores based on the payload")
    @ApiResponses(value = {@ApiResponse(code = 500, message = "Internal Server Exception")})

    public ReceiveSummary saveReceiveSummary(@RequestBody ReceivingSummaryRequest receivingSummaryRequest, @PathVariable("countryCode")
            String countryCode) {
        return receiveSummaryService.saveReceiveSummary(receivingSummaryRequest);

    }


    /**
     * ReceiveSummary
     * Method calls Receive Service to get
     *
     * @param
     * @return store
     */
    //TODO receiptNumbers :  as of now passing first object.
    // itemNumbers and upcNumbers : implementation are pending
    @GetMapping
    @ApiOperation(value = "API to add new Stores based on the payload")
    @ApiResponses(value = {@ApiResponse(code = 500, message = "Internal Server Error")})
    public List<ReceivingSummaryResponse> getReceiveSummary(@PathVariable("countryCode")
                                                                    String countryCode,
                                                            @RequestParam(value = "purchaseOrderNumber", required = false) String purchaseOrderNumber,
                                                            @RequestParam(value = "purchaseOrderId", required = false) String purchaseOrderId,
                                                            @RequestParam(value = "receiptNumbers", required = false) List<String> receiptNumbers,
                                                            @RequestParam(value = "transactionType", required = false) String transactionType,
                                                            @RequestParam(value = "controlNumber", required = false) String controlNumber,
                                                            @RequestParam(value = "locationNumber", required = false) String locationNumber,
                                                            @RequestParam(value = "divisionNumber", required = false) String divisionNumber,
                                                            @RequestParam(value = "vendorNumber", required = false) String vendorNumber,
                                                            @RequestParam(value = "departmentNumber", required = false) String departmentNumber,
                                                            @RequestParam(value = "invoiceId", required = false) String invoiceId,
                                                            @RequestParam(value = "invoiceNumber", required = false) String invoiceNumber,
                                                            @RequestParam(value = "receiptDateStart", required = false) String receiptDateStart,
                                                            @RequestParam(value = "receiptDateEnd", required = false) String receiptDateEnd,
                                                            @RequestParam(value = "itemNumbers", required = false) List<String> itemNumbers,
                                                            @RequestParam(value = "upcNumbers", required = false) List<String> upcNumbers
                                                            /*@RequestParam(value = "pageNbr", defaultValue = "0")
                                                                    Integer pageNbr,
                                                            @RequestParam(value = "pageSize", defaultValue = "1000")
                                                                    Integer pageSize,
                                                            @RequestParam(value = "orderBy", defaultValue = "creationDate")
                                                                    String orderBy,
                                                            @RequestParam(value = "order", defaultValue = "DESC")
                                                                    Sort.Direction order*/) {

        return receiveSummaryService.getReceiveSummary(countryCode, purchaseOrderNumber, purchaseOrderId, receiptNumbers, transactionType, controlNumber, locationNumber,
                divisionNumber, vendorNumber, departmentNumber, invoiceId, invoiceNumber, receiptDateStart, receiptDateEnd, itemNumbers, upcNumbers);//allRequestParam); , pageNbr, pageSize, orderBy, order
    }

    /**
     * Method calls Service class to add stores in Db
     *
     * @param
     * @return
     */
    @PutMapping
    @ApiOperation(value = "API to update Stores based on the payload")
    @ApiResponses(value = {@ApiResponse(code = 500, message = "Internal Server Exception")})
    public ReceivingSummaryRequest updateSummary(@PathVariable("countryCode") String countryCode, @RequestBody ReceivingSummaryRequest receivingSummaryRequest) {
        return receiveSummaryService.updateReceiveSummary(receivingSummaryRequest,receivingSummaryRequest.getVendorNumber(),countryCode);
    }




    /**
     * Method calls Service class to add stores in Db
     *
     * @param
     * @return
     */
    @PutMapping("/line")
    @ApiOperation(value = "API to update Stores based on the payload")
    @ApiResponses(value = {@ApiResponse(code = 500, message = "Internal Server Exception")})
    public ReceivingSummaryLineRequest updateSummaryAndLine(@PathVariable("countryCode") String countryCode, @RequestBody ReceivingSummaryLineRequest receiveSummaryLineRequest) {
        return receiveSummaryService.updateReceiveSummaryAndLine(receiveSummaryLineRequest,countryCode,receiveSummaryLineRequest.getVendorNumber());
    }


}

