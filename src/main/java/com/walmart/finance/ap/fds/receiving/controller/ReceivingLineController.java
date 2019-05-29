
package com.walmart.finance.ap.fds.receiving.controller;

import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.request.ReceivingLineRequest;
import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import com.walmart.finance.ap.fds.receiving.service.ReceiveLineService;
import com.walmart.finance.ap.fds.receiving.service.ReceiveLineServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(value = "{countryCode}/receiving/line")
@Api(value = "RESTful APIs for Receiving Line ")
public class ReceivingLineController {

    @Autowired
    private ReceiveLineService receiveLineService;

    @Autowired
    private ReceiveLineServiceImpl receiveLineServiceImpl;

   /* Method calls Receive Service to get

      @param
      @return store
    */

    @GetMapping
    @ApiOperation(value = "API to get new LineSummary based on the payload")
    @ApiResponses(value = {@ApiResponse(code = 500, message = "Internal Server Exception")})

    public List<ReceivingLineResponse> getReceiveLine(@PathVariable("countryCode")
                                                              String countryCode, @RequestParam(value = "purchaseOrderId", required = false) String purchaseOrderId,
                                                      @RequestParam(value = "receiptNumbers", required = false) String receiptNumbers,
                                                      @RequestParam(value = "transactionType", required = false) String transactionType,
                                                      @RequestParam(value = "controlNumber", required = false) String controlNumber,
                                                      @RequestParam(value = "locationNumber", required = false) String locationNumber,
                                                      @RequestParam(value = "divisionNumber", required = false) String divisionNumber,
                                                      @RequestParam(value = "pageNbr", defaultValue = "0")
                                                              Integer pageNbr,
                                                      @RequestParam(value = "pageSize", defaultValue = "1000")
                                                              Integer pageSize,
                                                      @RequestParam(value = "orderBy", defaultValue = "creationDate")
                                                              String orderBy,
                                                      @RequestParam(value = "order", defaultValue = "DESC")
                                                              Sort.Direction order) {

        return receiveLineService.getLineSummary(purchaseOrderId, receiptNumbers, transactionType, controlNumber, locationNumber, divisionNumber);

    }

    /**
     * Method calls Service class to add stores in Db
     *
     * @param
     * @return
     */
    @PostMapping
    @ApiOperation(value = "API to add new Stores based on the payload")
    @ApiResponses(value = {@ApiResponse(code = 500, message = "Internal Server Exception")})

    public ReceivingLine saveReceiveLine(@PathVariable("countryCode")
                                                 String countryCode, @RequestBody ReceivingLineRequest receivingLineRequest) {
        return receiveLineServiceImpl.saveReceiveLine(receivingLineRequest);

    }
}
