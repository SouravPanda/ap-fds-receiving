package com.walmart.finance.ap.fds.receiving.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class Freight {
    @Id
    private Long _id;
    private Integer vendorNbr;
    private String carrierCode;
    private String billNbr;
    private Double billCostAmt;
    private LocalDate billDate;
    private Long billQty;
    private Long billWght;
    private String costUomCode;
    private Integer pymtStatCode;
    private String qtyUomCode;
    private String wghtUomCode;
    private String trailerNbr;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdatedDate;
    private String createdSource;
    private String updatedSource;
    private LocalDateTime logTimeStamp;
}
