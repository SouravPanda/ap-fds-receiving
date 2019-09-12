package com.walmart.finance.ap.fds.receiving.common;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class ReceivingUtils {

    public static String[] getPartitionKeyList(String keyAttributeValue, Integer monthsToDisplay,
                                            Integer monthsPerShard ) {
        //keyAttributeValue is 'Store Number' for now

        List<String> partitionKeyList = new ArrayList<>();

        LocalDate currentLocalDate = LocalDate.now(ZoneId.of("UTC"));
        for (int i = 0; i < monthsToDisplay/monthsPerShard; i++) {

            partitionKeyList.add(getPartitionKey(keyAttributeValue, currentLocalDate, monthsPerShard));

            currentLocalDate = currentLocalDate.minusMonths(monthsPerShard);

        }
        return partitionKeyList.toArray(new String[0]);
    }

    public static String getPartitionKey(String docKeyField, LocalDate mdseRecvDate, Integer monthsPerShard) {

        StringBuilder partitionKeyBuilder = new StringBuilder();
        partitionKeyBuilder
                .append(docKeyField).append("|")
                .append(mdseRecvDate.getYear()).append("|")
                .append((mdseRecvDate.getMonth().getValue() - 1) / monthsPerShard);

        return partitionKeyBuilder.toString();

    }

}
