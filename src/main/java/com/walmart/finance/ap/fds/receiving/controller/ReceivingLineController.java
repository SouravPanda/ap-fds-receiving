
package com.walmart.finance.ap.fds.receiving.controller;

import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import com.walmart.finance.ap.fds.receiving.service.ReceiveLineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@RestController
@RequestMapping(value = "/receiving-line")
@Api(value = "RESTful APIs for Receiving Line ")
public class ReceivingLineController {

    @Autowired
    private ReceiveLineService receiveLineService;


   /* Method calls Receive Service to get

      @param
      @return store
    */

    @GetMapping
    @ApiOperation(value = "API to get new LineSummary based on the payload")
    @ApiResponses(value = {@ApiResponse(code = 500, message = "Internal Server Exception")})

    public ReceivingLineResponse getReceiveLine(@NotEmpty @NotNull @RequestParam("receivingControlNumber") String receivingControlNumber,
                                                @NotEmpty @NotNull @RequestParam("poReceiveId") String poReceiveId,
                                                @NotEmpty @NotNull @RequestParam("storeNumber") String storeNumber,
                                                @NotEmpty @NotNull @RequestParam("baseDivisionNumber") String baseDivisionNumber,
                                                @NotEmpty @NotNull @RequestParam("transactionType") String transactionType,
                                                @NotEmpty @NotNull @RequestParam("finalDate") String finalDate,
                                                @NotEmpty @NotNull @RequestParam("finalTime") String finalTime,
                                                @NotEmpty @NotNull @RequestParam("sequenceNumber") String sequenceNumber) {

        return receiveLineService.getLineSummary(receivingControlNumber, poReceiveId, storeNumber, baseDivisionNumber, transactionType, finalDate, finalTime, sequenceNumber);

    }

}


