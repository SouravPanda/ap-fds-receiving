package com.walmart.finance.ap.fds.receiving.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.walmart.finance.ap.fds.receiving.exception.BadRequestException;
import com.walmart.finance.ap.fds.receiving.exception.ReceivingErrors;
import com.walmart.finance.ap.fds.receiving.integrations.FinancialTxnIntegrationServiceImpl;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummaryCosmosDBParameters;
import com.walmart.finance.ap.fds.receiving.model.ReceivingLine;
import com.walmart.finance.ap.fds.receiving.response.ReceivingInfoResponseV1;
import com.walmart.finance.ap.fds.receiving.validator.ReceivingInfoRequestQueryParameters;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ReceivingUtils {

    public static String[] getPartitionKeyList(String keyAttributeValue, LocalDate endDate, Integer monthsToDisplay,
                                               Integer monthsPerShard ) {
        //keyAttributeValue is 'Store Number' for now

        List<String> partitionKeyList = new ArrayList<>();


        //Handling +3 and -3 months
        int numberOfPartitionKeys = monthsToDisplay/monthsPerShard;
        if (monthsPerShard == 1) {
            endDate.plusMonths(3);
            numberOfPartitionKeys += 3;
        } else if (monthsPerShard == 2) {
            endDate.plusMonths(2);
            numberOfPartitionKeys += 2;
        } else {
            endDate.plusMonths(1);
            numberOfPartitionKeys += 1;
        }



        for (int i = 0; i < numberOfPartitionKeys; i++) {
            partitionKeyList.add(getPartitionKey(keyAttributeValue, endDate, monthsPerShard));
            endDate = endDate.minusMonths(monthsPerShard);
        }

        return partitionKeyList.toArray(new String[0]);
    }

    public static String[] getPartitionKeyList(String keyAttributeValue, Integer monthsToDisplay,
                                               Integer monthsPerShard ) {
        return getPartitionKeyList(keyAttributeValue, LocalDate.now(ZoneId.of("UTC")), monthsToDisplay,
                monthsPerShard );
    }

    public static String getPartitionKey(String docKeyField, LocalDate dateForPartitionKey, Integer monthsPerShard) {

        StringBuilder partitionKeyBuilder = new StringBuilder();
        partitionKeyBuilder
                .append(docKeyField).append("|")
                .append(dateForPartitionKey.getYear()).append("|")
                .append((dateForPartitionKey.getMonth().getValue() - 1) / monthsPerShard);
        return partitionKeyBuilder.toString();

    }

    public static LocalDateTime getDate(String date) {
        try {
            DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern(ReceivingConstants.DATEFORMATTER);
            return LocalDateTime.parse(date, formatterDate);
        } catch (DateTimeParseException e) {
            throw new BadRequestException(ReceivingErrors.INVALIDDATATYPE.getParameterName(), ReceivingErrors.INVALIDQUERYPARAMS.getParameterName());
        }
    }

    public static void updateQueryForPartitionKey(LocalDate receiptDate, Map<String, String> allParams,
                                            Integer storeNumber, Query queryToUpdate, Integer monthsPerShard,
                                            Integer monthsToDisplay) {
        Criteria partitionKeyCriteria = getCriteriaForPartitionKey(receiptDate, allParams, storeNumber, monthsPerShard, monthsToDisplay);
        queryToUpdate.addCriteria(partitionKeyCriteria);
    }

    public static Criteria getCriteriaForPartitionKey(LocalDate receiptDate, Map<String, String> allParams, Integer storeNumber, Integer monthsPerShard, Integer monthsToDisplay) {
        Criteria partitionKeyCriteria;

        if (receiptDate != null) {
            /* This scenario will be applicable in case of 'Stores', where we get 'Receiving Date' as a part of
            response from FinTrans*/
            partitionKeyCriteria =
                    Criteria.where(ReceivingConstants.RECEIVING_SHARD_KEY_FIELD)
                            .is(ReceivingUtils.getPartitionKey(String.valueOf(storeNumber),
                                    receiptDate, monthsPerShard));
        } else if (allParams.containsKey(ReceivingInfoRequestQueryParameters.RECEIPTDATESTART.getQueryParam())
                && allParams.containsKey(ReceivingInfoRequestQueryParameters.RECEIPTDATEEND.getQueryParam())) {
            /* This flow will be applicable for requests which has 'receipt start date' and 'receipt end date' as a
            part of the request */
            LocalDateTime startDate = getDate(allParams.get(ReceivingConstants.RECEIPTDATESTART) + " 00:00:00");
            LocalDateTime endDate = getDate(allParams.get(ReceivingConstants.RECEIPTDATEEND) + " 00:00:00");
            Period diff = Period.between(startDate.toLocalDate(), endDate.toLocalDate());
            int adjustedMonthsTodDisplay =
                    new Double(Math.ceil((diff.getMonths() + 2) / monthsPerShard.doubleValue()) * monthsPerShard)
                            .intValue();
            partitionKeyCriteria =
                    Criteria.where(ReceivingConstants.RECEIVING_SHARD_KEY_FIELD)
                            .in(ReceivingUtils.getPartitionKeyList(String.valueOf(storeNumber),
                                    endDate.toLocalDate(), adjustedMonthsTodDisplay, monthsPerShard));
        } else {
            /* If non of the above conditions match, we search across all the shards for the configured number of
            months */
            partitionKeyCriteria =
                    Criteria.where(ReceivingConstants.RECEIVING_SHARD_KEY_FIELD)
                            .in(ReceivingUtils.getPartitionKeyList(String.valueOf(storeNumber),
                                    monthsToDisplay, monthsPerShard));
        }
        return partitionKeyCriteria;
    }

    public static List<String> getPartitionKeyList(LocalDate receiptDate, Map<String, String> allParams,
                                               Integer storeNumber, Integer monthsPerShard, Integer monthsToDisplay) {

        List<String> partitionKeyList = new ArrayList<>();
        if (receiptDate != null) {
            /* This scenario will be applicable in case of 'Stores', where we get 'Receiving Date' as a part of
            response from FinTrans*/
            partitionKeyList.add(ReceivingUtils.getPartitionKey(String.valueOf(storeNumber),
                                    receiptDate, monthsPerShard));
        } else if (allParams.containsKey(ReceivingInfoRequestQueryParameters.RECEIPTDATESTART.getQueryParam())
                && allParams.containsKey(ReceivingInfoRequestQueryParameters.RECEIPTDATEEND.getQueryParam())) {
            /* This flow will be applicable for requests which has 'receipt start date' and 'receipt end date' as a
            part of the request */
            LocalDateTime startDate = getDate(allParams.get(ReceivingConstants.RECEIPTDATESTART) + " 00:00:00");
            LocalDateTime endDate = getDate(allParams.get(ReceivingConstants.RECEIPTDATEEND) + " 00:00:00");
            Period diff = Period.between(startDate.toLocalDate(), endDate.toLocalDate());
            int adjustedMonthsTodDisplay =
                    new Double(Math.ceil((diff.getMonths() + 2) / monthsPerShard.doubleValue()) * monthsPerShard)
                            .intValue();

            partitionKeyList = Arrays.asList(ReceivingUtils.getPartitionKeyList(String.valueOf(storeNumber),
                                    endDate.toLocalDate(), adjustedMonthsTodDisplay, monthsPerShard));
        } else {
            /* If non of the above conditions match, we search across all the shards for the configured number of
            months */
            partitionKeyList = Arrays.asList(ReceivingUtils.getPartitionKeyList(String.valueOf(storeNumber),
                                    monthsToDisplay, monthsPerShard));
        }
        return partitionKeyList;
    }

    public static void updateLineResponse(List<ReceivingLine> lineResponseList) {
        for (ReceivingLine receivingLine : lineResponseList) {
            if (StringUtils.isNotEmpty(receivingLine.getUpcNumber()) && receivingLine.getUpcNumber().length() > 13) {
                /* Change 16 Digit GTIN number to 13 Digit UPC number */
                int lastDigit =
                        Integer.parseInt(
                                String.valueOf(receivingLine.getUpcNumber().charAt(receivingLine.getUpcNumber().length() - 1)));
                if (lastDigit > 0) {
                    receivingLine.setUpcNumber(receivingLine.getUpcNumber().substring(3));
                } else {
                    receivingLine.setUpcNumber(receivingLine.getUpcNumber().substring(2,
                            receivingLine.getUpcNumber().length() - 1));
                }
            }
        }
    }

    public static ReceivingInfoResponseV1 getRecvInfoRespV1Copy(ReceivingInfoResponseV1 receivingInfoResponseV1) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(objectMapper.writeValueAsString(receivingInfoResponseV1),
                    ReceivingInfoResponseV1.class);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }
}
