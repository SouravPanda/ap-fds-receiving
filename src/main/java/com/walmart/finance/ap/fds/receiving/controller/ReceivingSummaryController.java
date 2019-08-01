package com.walmart.finance.ap.fds.receiving.controller;

import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryLineRequest;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import com.walmart.finance.ap.fds.receiving.service.ReceiveSummaryService;
import com.walmart.finance.ap.fds.receiving.validator.ReceiveSummaryValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "{countryCode}/receiving/summary")
@Api(value = "REST APIs for receiving-summary ")
public class ReceivingSummaryController {

    @Autowired
    private ReceiveSummaryService receiveSummaryService;

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
    public ReceivingResponse getReceiveSummary(@PathVariable("countryCode")
                                                       String countryCode,
                                               @RequestParam Map<String, String> allRequestParams, List<String> itemNumbers, List<String> upcNumbers) {
        ReceiveSummaryValidator.validate(countryCode, allRequestParams);

        return receiveSummaryService.getReceiveSummary(allRequestParams, itemNumbers, upcNumbers);
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
    public ResponseEntity<ReceivingResponse> updateSummary(@PathVariable("countryCode") String countryCode, @RequestBody @Valid ReceivingSummaryRequest receivingSummaryRequest) {
        ReceivingResponse receivingResponse = receiveSummaryService.updateReceiveSummary(receivingSummaryRequest, countryCode);
        return new ResponseEntity<>(receivingResponse, HttpStatus.ACCEPTED);
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
    public ResponseEntity<ReceivingResponse> updateSummaryAndLine(@PathVariable("countryCode") String countryCode, @RequestBody @Valid ReceivingSummaryLineRequest receiveSummaryLineRequest) {
        ReceivingResponse receivingResponse = receiveSummaryService.updateReceiveSummaryAndLine(receiveSummaryLineRequest, countryCode);
        return new ResponseEntity<>(receivingResponse, HttpStatus.ACCEPTED);
    }
}

