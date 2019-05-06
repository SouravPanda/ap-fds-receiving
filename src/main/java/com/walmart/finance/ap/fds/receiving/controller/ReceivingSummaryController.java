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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@RestController
@RequestMapping(value = "/receiving-summary/{countryCode}")
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
    @ApiResponses(value = {@ApiResponse(code = 500, message = "Internal Server Error")})

    public ReceivingSummaryResponse getReceiveSummary(@NotEmpty @NotNull @RequestParam("receivingControlNumber")  String  receivingControlNumber  ,
                                                      @NotEmpty @NotNull @RequestParam("poReceiveId") String poReceiveId,
                                                      @NotEmpty @NotNull @RequestParam("storeNumber") String storeNumber,
                                                      @NotEmpty @NotNull  @RequestParam("baseDivisionNumber") String baseDivisionNumber,
                                                      @NotEmpty @NotNull  @RequestParam( "transactionType") String transactionType,
                                                      @NotEmpty @NotNull @RequestParam( "finalDate" ) String finalDate,
                                                      @NotEmpty @NotNull @RequestParam( "finalTime")  String finalTime){

        return receiveSummaryService.getReceiveSummary( receivingControlNumber,  poReceiveId,  storeNumber,  baseDivisionNumber,  transactionType,  finalDate, finalTime);

    }


    @PostMapping("/search")
    @ApiOperation(value = "API to search ReceivingSummary for given criteria")
    @ApiResponses(value = {@ApiResponse(code = 500, message = "Internal Server Error")})
    public Page<ReceivingSummaryResponse> getReceiveSummarySearch(

            @RequestParam(value = "pageNbr", defaultValue = "0" )
                    Integer pageNbr,
            @RequestParam(value = "pageSize", defaultValue = "10")
                    Integer pageSize,
            @RequestParam(value="orderBy", defaultValue="creationDate")
                    String orderBy,
            @RequestParam(value = "order", defaultValue = "DESC")
                    Sort.Direction order,
            @RequestBody ReceivingSummarySearch receivingSummarySearch){
        return receiveSummaryService.getReceiveSummarySearch(receivingSummarySearch,pageNbr,pageSize,orderBy,order);

    }



}

