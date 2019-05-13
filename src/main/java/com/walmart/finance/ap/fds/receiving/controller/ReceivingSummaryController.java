package com.walmart.finance.ap.fds.receiving.controller;

import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummarySearch;
import com.walmart.finance.ap.fds.receiving.response.ReceivingSummaryResponse;
import com.walmart.finance.ap.fds.receiving.service.ReceiveSummaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;


@RestController
//@RequestMapping(value = "{countryCode}/receiving/summary")
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
    @GetMapping
    @ApiOperation(value = "API to add new Stores based on the payload")
    @ApiResponses(value = {@ApiResponse(code = 500, message = "Internal Server Error")})

    public Page<ReceivingSummaryResponse> getReceiveSummary(@PathVariable("countryCode")
                                                                    String countryCode,
                                                            @RequestParam(value = "purchaseOrderNumber",required = false) String purchaseOrderNumber,
                                                            @RequestParam(value = "purchaseOrderId",required = false) String purchaseOrderId,
                                                            @RequestParam(value ="receiptNumber",required = false) String receiptNumber,
                                                            @RequestParam(value ="transactionType",required = false) String transactionType,
                                                            @RequestParam(value ="controlNumber",required = false) String controlNumber,
                                                            @RequestParam(value ="locationNumber",required = false) String locationNumber,
                                                            @RequestParam(value ="divisionNumber",required = false) String divisionNumber,
                                                            @RequestParam(value ="vendorNumber",required = false) String vendorNumber,
                                                            @RequestParam(value ="departmentNumber",required = false) String departmentNumber,
                                                            @RequestParam(value ="invoiceId",required = false) String invoiceId,
                                                            @RequestParam(value ="invoiceNumber",required = false) String invoiceNumber,
                                                            @RequestParam(value ="receiptDateStart",required = false) String receiptDateStart,
                                                            @RequestParam(value ="receiptDateEnd",required = false) String receiptDateEnd,
                                                            @RequestParam(value = "pageNbr", defaultValue = "0")
                                                                    Integer pageNbr,
                                                            @RequestParam(value = "pageSize", defaultValue = "10")
                                                                    Integer pageSize,
                                                            @RequestParam(value = "orderBy", defaultValue = "creationDate")
                                                                    String orderBy,
                                                            @RequestParam(value = "order", defaultValue = "DESC")
                                                                    Sort.Direction order) {

        return receiveSummaryService.getReceiveSummary(purchaseOrderNumber, purchaseOrderId, receiptNumber, transactionType, controlNumber, locationNumber,
                divisionNumber, vendorNumber, departmentNumber, invoiceId, invoiceNumber, receiptDateStart, receiptDateEnd, pageNbr, pageSize, orderBy, order);
    }


    @PostMapping("/search")
    @ApiOperation(value = "API to search ReceivingSummary for given criteria")
    @ApiResponses(value = {@ApiResponse(code = 500, message = "Internal Server Error")})
    public Page<ReceivingSummaryResponse> getReceiveSummarySearch(
            @PathVariable("countryCode")
                    String countryCode,
            @RequestParam(value = "pageNbr", defaultValue = "0")
                    Integer pageNbr,
            @RequestParam(value = "pageSize", defaultValue = "10")
                    Integer pageSize,
            @RequestParam(value = "orderBy", defaultValue = "creationDate")
                    String orderBy,
            @RequestParam(value = "order", defaultValue = "DESC")
                    Sort.Direction order,
            @RequestBody ReceivingSummarySearch receivingSummarySearch) {
        return receiveSummaryService.getReceiveSummarySearch(receivingSummarySearch, pageNbr, pageSize, orderBy, order);

    }

}

