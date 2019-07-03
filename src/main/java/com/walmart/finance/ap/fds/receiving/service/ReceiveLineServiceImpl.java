
package com.walmart.finance.ap.fds.receiving.service;

import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.walmart.finance.ap.fds.receiving.config.MongoConfig;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingLineResponseConverter;
import com.walmart.finance.ap.fds.receiving.exception.BadRequestException;
import com.walmart.finance.ap.fds.receiving.exception.NotFoundException;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.response.ReceivingLineResponse;
import com.walmart.finance.ap.fds.receiving.response.ReceivingResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.eq;

@Service
public class ReceiveLineServiceImpl implements ReceiveLineService {

    public static final Logger log = LoggerFactory.getLogger(ReceiveLineServiceImpl.class);

    @Autowired
    ReceivingLineResponseConverter receivingLineResponseConverter;

    @Autowired
    MongoConfig mongoConfig;

    public ReceivingResponse getLineSummary(String purchaseOrderId, String receiptNumber, String transactionType, String controlNumber, String locationNumber, String divisionNumber) {
        try {
            List<ReceivingLine> receiveLines = queryForLineResponseMongoClient(purchaseOrderId, receiptNumber, transactionType, controlNumber, locationNumber, divisionNumber);
            List<ReceivingLineResponse> responseList;
            if (CollectionUtils.isEmpty(receiveLines)) {
                throw new NotFoundException("Receiving line not found for given search criteria ", "please enter valid query parameters");
            } else {
                responseList = receiveLines.stream().map(t -> receivingLineResponseConverter.convert(t)).collect(Collectors.toList());
                ReceivingResponse successMessage = new ReceivingResponse();
                successMessage.setTimestamp(LocalDateTime.now());
                successMessage.setData(responseList);
                successMessage.setSuccess(true);
                return successMessage;
            }
        } catch (NumberFormatException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new BadRequestException("Data Type is invalid for input values.", "Please enter valid query parameters");
        }
    }

    /**
     * Configuration to use Monogo Client class for db call
     **/
    private List<ReceivingLine> queryForLineResponseMongoClient(String purchaseOrderId, String receiptNumber, String transactionType, String controlNumber, String locationNumber, String divisionNumber) {
        List<Bson> fields = new ArrayList<>();
        if (StringUtils.isNotEmpty(purchaseOrderId) || (StringUtils.isNotEmpty(controlNumber))) {
            if (StringUtils.isNotEmpty(purchaseOrderId)) {
                fields.add(eq("receivingControlNumber", purchaseOrderId));
            } else {
                fields.add(eq("receivingControlNumber", controlNumber));
            }
        }
        if (StringUtils.isNotEmpty(receiptNumber)) {
            fields.add(eq("purchaseOrderReceiveID", receiptNumber));
        }
        if (StringUtils.isNotEmpty(transactionType)) {
            fields.add(eq("transactionType", Integer.valueOf(transactionType)));
        }
        if (StringUtils.isNotEmpty(divisionNumber)) {
            fields.add(eq("baseDivisionNumber", Integer.valueOf(divisionNumber)));
        }
        if (StringUtils.isNotEmpty(locationNumber)) {
            fields.add(eq("storeNumber", Integer.valueOf(locationNumber)));
        }
        Bson bson = Filters.and(fields);
        log.info("Query is : "+bson.toString());
        FindIterable<Document> cursor = executeQueryInLineMongoClient(bson);
        List<ReceivingLine> receiveLines = conversionLogic(cursor);
        return receiveLines;
    }

    private List<ReceivingLine> conversionLogic(FindIterable<Document> cursor) {
        List<ReceivingLine> receiveLines = new ArrayList<>();
        MongoCursor<Document> iterator = cursor.iterator();
        Gson gson = new Gson();
        while (iterator.hasNext()) {
            Document document = iterator.next();
            receiveLines.add(gson.fromJson(document.toJson(), ReceivingLine.class));
        }
        return receiveLines;
    }

    private FindIterable<Document> executeQueryInLineMongoClient(Bson bson) {
        return mongoConfig.mongoClient().getDatabase(mongoConfig.getDatabaseName()).getCollection(mongoConfig.getLineCollection()).find(bson);
    }
}



