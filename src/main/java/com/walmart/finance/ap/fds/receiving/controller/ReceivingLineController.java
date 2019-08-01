
package com.walmart.finance.ap.fds.receiving.controller;

import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import com.walmart.finance.ap.fds.receiving.service.ReceiveLineService;
import com.walmart.finance.ap.fds.receiving.service.ReceiveLineServiceImpl;
import com.walmart.finance.ap.fds.receiving.validator.ReceiveLineValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping(value = "{countryCode}/receiving/line")
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

    public ReceivingResponse getReceiveLine(@PathVariable("countryCode")
                                                    String countryCode, @RequestParam Map<String, String> allRequestParams) {
        ReceiveLineValidator.validate(countryCode, allRequestParams);

        return receiveLineService.getLineSummary(allRequestParams);

    }

}
