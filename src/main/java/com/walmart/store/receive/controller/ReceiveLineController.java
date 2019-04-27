
package com.walmart.store.receive.controller;
import com.walmart.store.receive.Response.ReceivingLineResponse;
import com.walmart.store.receive.service.ReceiveLineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/receiving-line")
@Api(value = "RESTful APIs for receiveLineService ")
public class ReceiveLineController {

    @Autowired
    private ReceiveLineService receiveLineService;


   /* Method calls Receive Service to get

      @param
      @return store
    */

    @GetMapping
    @ApiOperation(value = "API to get new LineSummary based on the payload")
    @ApiResponses(value = {@ApiResponse(code = 500, message = "Internal Server Exception")})

    public ReceivingLineResponse getReceiveLine(@RequestParam("receivingControlNumber")  String receivingControlNumber  ,
                                                   @RequestParam("poReceiveId") String poReceiveId,
                                                   @RequestParam("storeNumber") String storeNumber,
                                                   @RequestParam("baseDivisionNumber") String baseDivisionNumber,
                                                   @RequestParam("transactionType") String transactionType,
                                                   @RequestParam("finalDate") String finalDate,
                                                   @RequestParam("finalTime") String finalTime,
                                                   @RequestParam("sequenceNumber")String sequenceNumber){

        return receiveLineService.getLineSummary( receivingControlNumber,  poReceiveId,  storeNumber,  baseDivisionNumber,  transactionType,  finalDate, finalTime, sequenceNumber);

    }

}


