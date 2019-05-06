
package com.walmart.finance.ap.fds.receiving.controller;

import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.request.ReceiveLineSearch;
import com.walmart.finance.ap.fds.receiving.request.ReceivingLineRequest;
import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import com.walmart.finance.ap.fds.receiving.service.ReceiveLineService;
import com.walmart.finance.ap.fds.receiving.service.ReceiveLineServiceImpl;
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
@RequestMapping(value = "/receiving/line/countryCode/{countryCode}")
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

    /**
     * Method calls Service class to add stores in Db
     *
     * @param
     * @return
     */
    @PostMapping
    @ApiOperation(value = "API to add new Stores based on the payload")
    @ApiResponses(value = {@ApiResponse(code = 500, message = "Internal Server Exception")})

    public ReceivingLine saveReceiveLine(@RequestBody ReceivingLineRequest receivingLineRequest) {
        return receiveLineServiceImpl.saveReceiveLine(receivingLineRequest);

    }

    /**
     * Method calls Service class to search receiveLine in Db
     *
     * @param
     * @return
     */

    @PostMapping("/search")
    @ApiOperation(value = "API to search ReceivingSummary for given criteria")
    @ApiResponses(value = {@ApiResponse(code = 500, message = "Internal Server Error")})
    public Page<ReceivingLineResponse> getReceiveLineSearch(

            @RequestParam(value = "pageNbr", defaultValue = "0" )
                    Integer pageNbr,
            @RequestParam(value = "pageSize", defaultValue = "10")
                    Integer pageSize,
            @RequestParam(value="orderBy", defaultValue="creationDate")
                    String orderBy,
            @RequestParam(value = "order", defaultValue = "DESC")
                    Sort.Direction order,
            @RequestBody ReceiveLineSearch receivingLineSearch){
        return receiveLineService.getReceiveLineSearch(receivingLineSearch,pageNbr,pageSize,orderBy,order);

    }

}
