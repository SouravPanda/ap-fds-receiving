package com.walmart.finance.ap.fds.receiving.controller;

import com.walmart.finance.ap.fds.receiving.integrations.FreightResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import com.walmart.finance.ap.fds.receiving.service.FreightService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = ReceivingResponse.class))),
            @ApiResponse(responseCode = "204"),
            @ApiResponse(responseCode = "400", content = @Content(schema = @Schema(implementation = ReceivingResponse.class))),
            @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ReceivingResponse.class)))})
    public ReceivingResponse getFreightById(@PathVariable(value = "id") String id) {
        return freightService.getFreightById(id);

    }

}
