package com.walmart.finance.ap.fds.receiving.controller;

import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import com.walmart.finance.ap.fds.receiving.service.ReceivingInfoService;
import com.walmart.finance.ap.fds.receiving.validator.ReceivingInfoRequestValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * This controller is to integrate financial transaction service.
 */
@RestController
@RequestMapping(value = "{countryCode}/receiving/info")
@Api(value = "RESTful APIs for Receiving Info ")
public class ReceivingInfoController {

    @Autowired
    ReceivingInfoService receivingInfoService;

    @GetMapping
    @ApiOperation(value = "API to receive information based on various parameters.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ReceivingResponse.class))),
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ReceivingResponse.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ReceivingResponse.class)))})
    @RequestMapping(value = "/v1", method = RequestMethod.GET)
    public ReceivingResponse getReceivingInfoV1(@PathVariable(value = "countryCode") String countryCode
            , @RequestParam Map<String, String> allRequestParams) {
        ReceivingInfoRequestValidator.validate(countryCode, allRequestParams);
        return receivingInfoService.getInfoServiceDataV1(allRequestParams);
    }
}
