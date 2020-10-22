
package com.walmart.finance.ap.fds.receiving.controller;

import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import com.walmart.finance.ap.fds.receiving.service.ReceiveLineService;
import com.walmart.finance.ap.fds.receiving.validator.ReceiveLineValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "{countryCode}/receiving/line")
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ReceivingResponse.class))),
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ReceivingResponse.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ReceivingResponse.class)))})

    public ReceivingResponse getReceiveLine(@PathVariable("countryCode")
                                                    String countryCode, @RequestParam Map<String, String> allRequestParams) {
        ReceiveLineValidator.validate(countryCode, allRequestParams);

        return receiveLineService.getLineSummary(allRequestParams);

    }
}
