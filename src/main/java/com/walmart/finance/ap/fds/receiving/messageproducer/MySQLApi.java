package com.walmart.finance.ap.fds.receiving.messageproducer;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import com.walmart.finance.ap.fds.receiving.response.SuccessMessage;
import com.walmart.finance.audit.rest.model.AuditFailureRequest;
import com.walmart.finance.audit.rest.model.AuditFailureResponse;
import com.walmart.finance.audit.rest.model.ErrorAuditFailureRequest;
import com.walmart.finance.audit.rest.model.MetaAuditFailureRequest;
import com.walmart.finance.audit.rest.service.AuditAPIService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class MySQLApi {


    @Autowired
    private AuditAPIService auditAPIService;

    public static final Logger log = LoggerFactory.getLogger(MySQLApi.class);

    public void saveFailureRecordTOMysql(SuccessMessage message) {

        AuditFailureRequest auditFailureRequest = new AuditFailureRequest();
        auditFailureRequest.setPayload(message.getPayload());
        auditFailureRequest.setSuccess(ReceivingConstants.FALSE);
        auditFailureRequest.set_id(message.get_id());
        auditFailureRequest.setPartitionKey(message.getPartitionKey());
        auditFailureRequest.setDomain(message.getDomain());
        auditFailureRequest.setServiceFailed("cosmosWrite");
        auditFailureRequest.setObjectName(message.getObjectName());
        ErrorAuditFailureRequest err = new ErrorAuditFailureRequest();
        err.setDetails(ReceivingConstants.EVENT_HUB_NETWORK_ERROR);
        err.setMessage(ReceivingConstants.EVENT_HUB_NETWORK_ERROR);
        err.setErrorCode(ReceivingConstants.ERROR_CODE);
        auditFailureRequest.setError(err);
        auditFailureRequest.setMessageTimeStamp(message.getMessageTimeStamp());
        MetaAuditFailureRequest meta = new MetaAuditFailureRequest();
        meta.setUnitOfWorkId(message.getMeta().getUnitOfWorkId());
        auditFailureRequest.setMeta(meta);
        auditFailureRequest.setOperation(message.getOperation());
        try {
            ResponseEntity<AuditFailureResponse> auditFailureResponse = auditAPIService.saveRecordsForAudit(auditFailureRequest);
            log.info("Successfully updated the MySQL failure table with the failure record  %s and the response is %s",auditFailureRequest,auditFailureResponse);

        } catch (Exception exe) {
            log.error("exception while calling Audit API to save the failure record to MySQL failure table %s ",exe );
        }
    }
}
