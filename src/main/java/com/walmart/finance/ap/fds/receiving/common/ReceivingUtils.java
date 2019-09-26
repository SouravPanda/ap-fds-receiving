package com.walmart.finance.ap.fds.receiving.common;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class ReceivingUtils {

    public static String[] getPartitionKeyList(String keyAttributeValue, LocalDate endDate, Integer monthsToDisplay,
                                               Integer monthsPerShard ) {
        //keyAttributeValue is 'Store Number' for now

        List<String> partitionKeyList = new ArrayList<>();

        for (int i = 0; i < monthsToDisplay/monthsPerShard; i++) {
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
}
