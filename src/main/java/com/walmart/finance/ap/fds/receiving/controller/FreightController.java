package com.walmart.finance.ap.fds.receiving.controller;

import com.walmart.finance.ap.fds.receiving.integrations.FreightResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import com.walmart.finance.ap.fds.receiving.service.FreightService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

@RequestMapping(value = "{countryCode}/receiving/freight/{id}")
@Api(value = "REST APIs for receiving-summary ")
public class FreightController {

    @Autowired
    private FreightService freightService;

    @GetMapping
    @ApiOperation(value = "API to return freight information based on freight bill Id.")
    @ApiResponses(value = {@ApiResponse(code = 500, message = "Internal Server Error"), @ApiResponse(code = 400, message = "Bad request"), @ApiResponse(code = 200, message = "OK"), @ApiResponse(code = 204, message = "No content")})
    public ReceivingResponse getFreightById(@PathVariable(value = "id") String id) {
        return freightService.getFreightById(id);

    }

}
