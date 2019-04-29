/*
    package com.walmart.store.receive.validator;

    import ReceiveMDS;
    import ReceiveSummary;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.*;
    import org.springframework.web.client.RestTemplate;

    import java.net.URL;
    import java.util.Arrays;

    public class ServiceValidator {
        @Autowired
        RestTemplate restTemplate;
        private org.springframework.http.HttpEntity<?> HttpEntity;

        public void validateStore(ReceiveSummary store){
                URL url = null;
            ReceiveMDS receiveMDS= new ReceiveMDS();
            ResponseEntity response = null;
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> httpEntity = new HttpEntity<>(headers);
                response = restTemplate.exchange("https://api.dev.wal-mart.com/bofap/dev/bofap/api/supplier/v1/supplierNumber/122663/countryCode/US", HttpMethod.GET,httpEntity, ReceiveSummary.class);
                Object obj=response.getBody();
              if(store!=null) {
                        if(!(store.getTransactionType().equals(99))||(store.getTransactionType()>=0 && store.getTransactionType()<6)){
                            throw new RuntimeException("TransactionType Not Valid");
                        }
                        if(!(store.getControlType().equals(10))||(store.getControlType()>=0 && store.getControlType()<4)){
                            throw new RuntimeException("Control Type Not Valid");
                        }
                        if(!(store.getReceiveExpense().isEmpty())){
                            if(!(receiveMDS.getMdseConditionCode()>=0 && receiveMDS.getMdseConditionCode()<6)){
                                throw new RuntimeException("MDSE Condition Code Not Valid");
                            }
                            if((receiveMDS.getMdseQuantity()==0)){
                                throw new RuntimeException("MDSE Quantity Not Valid");
                            }
                            if(((int)receiveMDS.getMdseQuantityUnitOfMeasureCode()==0)){
                                throw new RuntimeException("MdseQuantityUnitOfMeasureCode Not Valid");
                            }
                        }
                  if(obj.equals(store.getVendorNumber())){
                      if(!(store.getTransactionType().equals(0)||store.getTransactionType().equals(1))){
                          throw new RuntimeException("Invalid VendorNumber");
                      }

                  }
                }
            }

        }
*/
