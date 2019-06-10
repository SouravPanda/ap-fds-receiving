package com.walmart.finance.ap.fds.receiving.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class ReceivingSummaryLineRequest {

    //Key2
  //  @NotNull(message = "receiptNumber cannot be null")
   @Size(max = 10, min = 1)
    @NotEmpty(message = "Please enter a valid receiptNumber")
    String receiptNumber;

    // Key1
   // @NotNull(message = "controlNumber cannot be null")
    @Size(max = 10, min = 1)
    @NotEmpty(message = "Please enter a valid controlNumber")
    String controlNumber;

    //Key3
    @NotNull(message = "receiptDate cannot be null")
   // @Size(max = 10, min = 1)
  //  @NotEmpty(message = "Please enter a valid receiptDate")
    private LocalDate receiptDate;

    //key4
    @NotNull(message = "locationNumber cannot be null")
   // @Size(max = 10, min = 1)
  //  @NotEmpty(message = "Please enter a valid locationNumber")
    Integer locationNumber;

    //@NotNull(message = "businessStatusCode cannot be null")
    @Size(max = 1, min = 1)
    @NotEmpty(message = "Please enter a valid businessStatusCode")
  //  @Range(min = 1,max = 1)
    String businessStatusCode;


 //   @NotNull
 //   @NotEmpty
    Integer sequenceNumber;


    @NotNull(message = "inventoryMatchStatus cannot be null")
  //  @Size(max = 10, min = 1)
 //   @NotEmpty(message = "Please enter a valid inventoryMatchStatus")
    Integer inventoryMatchStatus;

    Meta meta;

}
