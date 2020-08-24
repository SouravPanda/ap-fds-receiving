package com.walmart.finance.ap.fds.receiving.dao.queryCriteria;

import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import org.junit.Test;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FreightCriteriaTest {

    @Test
    public void testFreightCriteria() {

        List<ReceiveSummary> receiveSummaries = new ArrayList<>(Arrays.asList(prepareReceiveSummary()));

        List<Long> freightCriteria = FreightCriteria.getFreightCriteria(receiveSummaries);

        Assert.notNull(freightCriteria,"freight criteria");

    }

    private ReceiveSummary prepareReceiveSummary() {

        /*ReceiveSummary receiveSummary = new ReceiveSummary("4665267|1804823|8264|18|18|1995-10-17|18:45:21",
                "4665267", 3669, 18, 99,
                LocalDate.of(1996, 12, 12), LocalTime.of(18, 45, 21),
                0, 7688, 1111, 0, 0,
                "H", 0.0, 99.0, 'A',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56,
                22), LocalDate.of(2019, 03, 14),
                LocalDate.now(), 9.0, 7, "0",
                0, LocalDateTime.now(), 0, "0000030006", "yyyy",
                LocalDateTime.now(), "4665267"
                , 'K', "LLL", 0.0, new Long(999403403), null, null, null, LocalDateTime.of(2019, 03, 14, 8, 45, 21),
                null);
*/
        ReceiveSummary receiveSummary = new ReceiveSummary("4665267|1804823|8264|18|18|1995-10-17|18:45:21",
                "4665267", 3669, 18, 99,
                LocalDate.of(1996, 12, 12), LocalTime.of(18, 45, 21),
                0, 7688, 1111, 0, 0,
                "H", 0.0, 99.0, 'A',
                2L, 'k', 'L',
                'M', LocalDateTime.of(1990, 12, 12, 18, 56,
                22), LocalDate.of(2019, 03, 14),
                LocalDate.now(), 9.0, 7, "0",
                0, LocalDateTime.now(), 0, "0000030006", "yyyy",
                LocalDateTime.now(), "4665267"
                , 'K', "LLL", 0.0, new Long(999403403), null, null, null, LocalDateTime.of(2019, 03, 14, 8, 45, 21),
                null,null);

        return receiveSummary;
    }
}
