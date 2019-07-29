package com.walmart.finance.ap.fds.receiving.controller;

import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import com.walmart.finance.ap.fds.receiving.service.ReceivingInfoService;
import com.walmart.finance.ap.fds.receiving.validator.ReceivingInfoRequestValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
    @ApiResponses(value = {@ApiResponse(code = 500, message = "Internal Server Error"), @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 204, message = "No content")})
    public ReceivingResponse getReceivingInfo(@PathVariable(value = "countryCode") String countryCode
            , @RequestParam Map<String, String> allRequestParams) {
        ReceivingInfoRequestValidator.validate(countryCode, allRequestParams);
        return receivingInfoService.getInfoSeviceData(allRequestParams);
    }

    @GetMapping
    @ApiOperation(value = "API to receive information based on various parameters.")
    @ApiResponses(value = {@ApiResponse(code = 500, message = "Internal Server Error"), @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 204, message = "No content")})
    @RequestMapping(value = "/v1")
    public ReceivingResponse getReceivingInfoV1(@PathVariable(value = "countryCode") String countryCode
            , @RequestParam Map<String, String> allRequestParams) {
        ReceivingInfoRequestValidator.validate(countryCode, allRequestParams);
        return receivingInfoService.getInfoSeviceDataV1(allRequestParams);
    }
}
