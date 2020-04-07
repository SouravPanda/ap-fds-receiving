package com.walmart.finance.ap.fds.receiving.integrations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FreightResponse {

    private Long freightId;
    private String carrierCode;
    private String trailerNbr;
    private Integer vendorNbr;
    private String billNbr;
    private Double billCostAmt;
    private LocalDate billDate;
    private Long billQty;
    private Long billWght;
    private String costUomCode;
    private Integer pymtStatCode;
    private String qtyUomCode;
    private String wghtUomCode;


}
