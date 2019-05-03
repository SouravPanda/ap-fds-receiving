package com.walmart.finance.ap.fds.receiving.service;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.exception.ContentNotFoundException;
import com.walmart.finance.ap.fds.receiving.integrations.InvoiceIntegrationService;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummarySearch;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.walmart.finance.ap.fds.receiving.model.ReceiveSummary;
import com.walmart.finance.ap.fds.receiving.repository.ReceiveSummaryDataRepository;
import com.walmart.finance.ap.fds.receiving.request.ReceivingSummaryRequest;
import com.walmart.finance.ap.fds.receiving.response.ReceivingSummaryResponse;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryReqConverter;
import com.walmart.finance.ap.fds.receiving.converter.ReceivingSummaryResponseConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ReceiveSummaryServiceImpl implements ReceiveSummaryService {

    public static final Logger log = LoggerFactory.getLogger(ReceiveSummaryServiceImpl.class);

    private static final String separator = "|";

    @Autowired
    ReceiveSummaryDataRepository receiveDataRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    ReceivingSummaryResponseConverter receivingSummaryResponseConverter;

    @Autowired
    ReceivingSummaryReqConverter receivingSummaryReqConverter;

    @Autowired
    InvoiceIntegrationService invoiceIntegrationService;


    // TODO validation for incoming against MDM needs to be added later

    public ReceiveSummary saveReceiveSummary(ReceivingSummaryRequest receivingSummaryRequest) {
        ReceiveSummary receiveSummary = receivingSummaryReqConverter.convert(receivingSummaryRequest);
        return receiveDataRepository.save(receiveSummary);

    }


    public ReceivingSummaryResponse getReceiveSummary(String receivingControlNumber, String poReceiveId, String storeNumber, String baseDivisionNumber, String transactionType, String finalDate, String finalTime) {
        String id = formulateId(receivingControlNumber, poReceiveId, storeNumber, baseDivisionNumber, transactionType, finalDate, finalTime);
        log.info("id is "+id);
        Optional<ReceiveSummary> receiveSummary = receiveDataRepository.findById(id);
        if (receiveSummary.isPresent()) {
            ReceiveSummary savedReceiveSummary = receiveSummary.get();
            ReceivingSummaryResponse response = receivingSummaryResponseConverter.convert(savedReceiveSummary);
            return response;

        } else {
            throw new ContentNotFoundException("No content found");

        }
    }

    @Override
    public ReceivingSummaryResponse getReceiveSummarySearch(ReceivingSummarySearch receivingSummarySearch) {

        String searchCriteria =null;

        if (null != receivingSummarySearch) {

            if(receivingSummarySearch.getInvoiceNumber()!=null ||receivingSummarySearch.getInvoiceId()!=null){


                //TODO invoiceIntegrationService implemnation needed

                //invoiceIntegrationService


            }


            /*searchCriteria = receivingSummarySearch.getControlNumber() != null && !receivingSummarySearch.getControlNumber().isEmpty() ? receivingSummarySearch.getControlNumber() : "NA";
            searchCriteria += receivingSummarySearch.getInvoiceNumber() != null && receivingSummarySearch.getInvoiceNumber().isEmpty() ? receivingSummarySearch.getInvoiceNumber() : "NA";
            searchCriteria +=receivingSummarySearch.getPurchaseOrderNumber() !=null && receivingSummarySearch.getPurchaseOrderNumber().isEmpty() ? receivingSummarySearch.getPurchaseOrderNumber() : "NA";
            searchCriteria +=receivingSummarySearch.getCountryCode() !=null && receivingSummarySearch.getCountryCode()==0 ? receivingSummarySearch.getPurchaseOrderNumber() : "NA";
            searchCriteria +=receivingSummarySearch.getDepartmentNumber()!=null && receivingSummarySearch.getDepartmentNumber()==0 ? receivingSummarySearch.getDepartmentNumber() : "NA";
            searchCriteria +=receivingSummarySearch.getDivisionNumber()!=null && receivingSummarySearch.getDivisionNumber()==0 ? receivingSummarySearch.getDivisionNumber():"NA";
            searchCriteria +=receivingSummarySearch.getInvoiceId()!=null && receivingSummarySearch.getInvoiceId()==0 ? receivingSummarySearch.getInvoiceId():"NA";
            searchCriteria +=receivingSummarySearch.getPurchaseOrderId()!=null && receivingSummarySearch.getPurchaseOrderId()==0 ? receivingSummarySearch.getPurchaseOrderId():"NA";
            searchCriteria +=receivingSummarySearch.getLocationNumber()!=null && receivingSummarySearch.getLocationNumber()==0 ? receivingSummarySearch.getLocationNumber():"NA";
            searchCriteria +=receivingSummarySearch.getReceiptDateEnd()!=null && receivingSummarySearch.getReceiptDateEnd().equals(0)? receivingSummarySearch.getReceiptDateEnd():"NA";
            searchCriteria +=receivingSummarySearch.getReceiptDateStart()!=null && receivingSummarySearch.getReceiptDateStart().equals(0) ? receivingSummarySearch.getReceiptDateStart():"NA";
            searchCriteria +=receivingSummarySearch.getTransactionType()!=null && receivingSummarySearch.getTransactionType()==0 ?receivingSummarySearch.getTransactionType():"NA";
            searchCriteria +=receivingSummarySearch.getReceiptNumber()!=null && receivingSummarySearch.getReceiptNumber()==0 ? receivingSummarySearch.getReceiptNumber():"NA";
*/
        }

        Query dynamicQuery =new Query();

        if(!receivingSummarySearch.getControlNumber().isEmpty()){

            Criteria criteria =Criteria.where("receivingControlNumber").is(receivingSummarySearch.getControlNumber());
            dynamicQuery.addCriteria(criteria);
        }

        if(receivingSummarySearch.getPurchaseOrderId()!=0){

            Criteria criteria =Criteria.where("receivingControlNumber").is(receivingSummarySearch.getPurchaseOrderId());
            dynamicQuery.addCriteria(criteria);
        }

        if(receivingSummarySearch.getDivisionNumber()!=0){

            Criteria criteria =Criteria.where("baseDivisonNumber").is(receivingSummarySearch.getDivisionNumber());
            dynamicQuery.addCriteria(criteria);
        }

        if(!receivingSummarySearch.getReceiptDateStart().equals(0)){

            Criteria criteria =Criteria.where("mdsReceiveDate").is(receivingSummarySearch.getReceiptDateStart());
            dynamicQuery.addCriteria(criteria);
        }
//TODO need to check Cosmos has only MDSReceiveDate
        if(!receivingSummarySearch.getReceiptDateEnd().equals(0)){

            Criteria criteria =Criteria.where("mdsReceiveDate").is(receivingSummarySearch.getReceiptDateEnd());
            dynamicQuery.addCriteria(criteria);
        }

        if(receivingSummarySearch.getTransactionType()!=0){

            Criteria criteria =Criteria.where("transactionType").is(receivingSummarySearch.getTransactionType());
            dynamicQuery.addCriteria(criteria);
        }

        if(receivingSummarySearch.getLocationNumber()!=0){

            Criteria criteria =Criteria.where("storeNumber").is(receivingSummarySearch.getLocationNumber());
            dynamicQuery.addCriteria(criteria);
        }

        if(!receivingSummarySearch.getPurchaseOrderNumber().isEmpty()){

            Criteria criteria =Criteria.where("purchaseOrderNumber").is(receivingSummarySearch.getPurchaseOrderNumber());
            dynamicQuery.addCriteria(criteria);
        }

        if(receivingSummarySearch.getReceiptNumber()!=0){

            Criteria criteria =Criteria.where("poReceivingId").is(receivingSummarySearch.getReceiptNumber());
            dynamicQuery.addCriteria(criteria);
        }

        if(receivingSummarySearch.getDepartmentNumber()!=0){

            Criteria criteria =Criteria.where("departmentNumber").is(receivingSummarySearch.getDepartmentNumber());
            dynamicQuery.addCriteria(criteria);
        }

        if(receivingSummarySearch.getVendorNumber()!=0){
            Criteria criteria =Criteria.where("vendorNumber").is(receivingSummarySearch.getVendorNumber());
            dynamicQuery.addCriteria(criteria);
        }
        List<ReceiveSummary> receiveSummaries  =mongoTemplate.find(dynamicQuery,ReceiveSummary.class);

        ReceivingSummaryResponse receivingSummaryResponse= new ReceivingSummaryResponse();

        return receivingSummaryResponse;

    }


    private String formulateId(String receivingControlNumber, String poReceiveId, String storeNumber, String baseDivisionNumber, String transactionType, String finalDate, String finalTime) {
        return receivingControlNumber + ReceivingConstants.PIPE_SEPARATOR + poReceiveId + ReceivingConstants.PIPE_SEPARATOR + storeNumber + ReceivingConstants.PIPE_SEPARATOR +  baseDivisionNumber + ReceivingConstants.PIPE_SEPARATOR + transactionType + ReceivingConstants.PIPE_SEPARATOR +  finalDate + ReceivingConstants.PIPE_SEPARATOR +  finalTime;


    }




}


