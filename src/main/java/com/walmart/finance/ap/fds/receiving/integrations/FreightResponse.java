package com.walmart.finance.ap.fds.receiving.integrations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection="receive-freight")
public class FreightResponse {

    private String _id;

    private String carrierCode;

    private String trailerNbr;


}
