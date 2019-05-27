package com.walmart.finance.ap.fds.receiving.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class FoundationSupplierWrapper implements Serializable {
    @JsonProperty("supplierList")
    private List<FoundationSupplier> foundationSupplierList;
}
