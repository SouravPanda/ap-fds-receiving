package com.walmart.store.receive.controller;
import com.walmart.store.receive.Request.ReceivingSummaryRequest;
import com.walmart.store.receive.Response.ReceivingSummaryResponse;
import com.walmart.store.receive.pojo.ReceiveSummary;
import com.walmart.store.receive.service.ReceiveSummaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/receiving-summary")
@Api(value = "RESTful APIs for receiveSummaryService ")
public class ReceiveSummaryController {

    @Autowired
    private ReceiveSummaryService receiveSummaryService;


   /* @RequestMapping(method = {RequestMethod.PATCH}, value = "/receiveSummary")
    @ApiOperation(value = "API to update Stores based on the Vendor Number")
    @ApiResponses(value = {@ApiResponse(code = 500, message = "Internal Server Exception")})
    public List<ReceiveSummary> updateStores(@RequestBody ReceivingSummaryRequest receivingSummaryRequest) {
        return receiveSummaryService.updateReceiveSummary(receivingSummaryRequest);
    }*/

    /**
     * Method calls Service class to add stores in Db
     *
     * @param
     * @return
     */
    @PostMapping
    @ApiOperation(value = "API to add new Stores based on the payload")
    @ApiResponses(value = {@ApiResponse(code = 500, message = "Internal Server Exception")})

    public ReceiveSummary saveReceiveSummary(@RequestBody ReceivingSummaryRequest receivingSummaryRequest) {
       return receiveSummaryService.saveReceiveSummary(receivingSummaryRequest);

    }


    /** ReceiveSummary
     *  Method calls Receive Service to get
     *
     * @param
     * @return store
     */
    @GetMapping
    @ApiOperation(value = "API to add new Stores based on the payload")
    @ApiResponses(value = {@ApiResponse(code = 500, message = "Internal Server Exception")})

    public ReceivingSummaryResponse getReceiveSummary(@RequestParam("receivingControlNumber")  String receivingControlNumber  ,
                                                      @RequestParam("poReceiveId") String poReceiveId,
                                                      @RequestParam("storeNumber") String storeNumber,
                                                      @RequestParam("baseDivisionNumber") String baseDivisionNumber,
                                                      @RequestParam("transactionType") String transactionType,
                                                      @RequestParam("finalDate") String finalDate,
                                                      @RequestParam("finalTime") String finalTime){


        // receivingControlNumber=4665265&poReceiveId=1804823&storeNumber=8264&baseDivisionNumber=18&transactionType=3&finalDate=1995-10-17&finalTime=18:45:21

        return receiveSummaryService.getReceiveSummary( receivingControlNumber,  poReceiveId,  storeNumber,  baseDivisionNumber,  transactionType,  finalDate, finalTime);

    }

}

