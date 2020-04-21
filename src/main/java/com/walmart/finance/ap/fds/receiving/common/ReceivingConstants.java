package com.walmart.finance.ap.fds.receiving.common;


import com.google.common.collect.ImmutableList;

public class ReceivingConstants {

    public static final String ACCEPT = "Accept";
    public static final String WALMART_ITEM_JSON_ACCEPT = "application/json";
    public static final String RECEIVINGCONTROLNUMBER = "controlNumber";
    public static final String RECEIVEID = "receiveId";
    public static final String STORENUMBER = "locationNumber";
    public static final String BASEDIVISONNUMBER = "baseDivisionNumber";
    public static final String STORE = "ReceiveSummary";
    public static final String AP_FDS_CLIENT_ID = "client_id";
    public static final String CORRELATION_ID_HEADER_KEY = "X-Correlation-ID" ;
    public static final String PIPE_SEPARATOR = "|";

    public static final String SM_WM_CONSUMER = "WM_CONSUMER.ID";
    public static final String SM_WM_ENV = "WM_SVC.ENV";
    public static final String SM_WM_APP_NAME = "WM_SVC.NAME";
    public static final String SM_WM_KEY_VERSION = "WM_SEC.KEY_VERSION";
    public static final String SM_AUTH_SIGN = "WM_SEC.AUTH_SIGNATURE";
    public static final String SM_INVOCATION_TS = "WM_CONSUMER.INTIMESTAMP";

    public static final String WM_CONSUMER = "WMT-API-SECRET";  //"WM_CONSUMER.ID";
    public static final String WMAPIKEY = "WMT-API-KEY";
    public static final String RECEIVESUMMARYWAREHOUSE = "receiving-wh-summary-db2";
    public static final String RECEIVELINEWAREHOUSE = "receiving-wh-summaryline-db2";
    public static final int XSS_FILTER_ORDER = 1;
    public static final int MDC_FILTER_ORDER = 2;
    public static final int LOGGING_FILTER_ORDER = 3;
    public static final int PERF_LOGGING_FILTER_ORDER = 4;
    public static final String COUNTRYCODE = "countryCode";
    public static final String INVOICENUMBER = "invoiceNumber";
    public static final String PURCHASEORDERNUMBER = "purchaseOrderNumber";
    public static final String PURCHASEORDERID = "purchaseOrderId";
    public static final String RECEIPTNUMBERS = "receiptNumbers";
    public static final String RECEIPTNUMBER = "receiptNumber";
    public static final String TRANSACTIONTYPE = "transactionType";
    public static final String CONTROLNUMBER = "controlNumber";
    public static final String LOCATIONNUMBER = "locationNumber";
    public static final String DIVISIONNUMBER = "divisionNumber";
    public static final String VENDORNUMBER = "vendorNumber";
    public static final String DEPARTMENTNUMBER = "departmentNumber";
    public static final String INVOICEID = "invoiceId";
    public static final String RECEIPTDATEEND = "receiptDateEnd";
    public static final String RECEIPTDATESTART = "receiptDateStart";
    public static final String ITEMNUMBERS="itemNumbers";
    public static final String UPCNUMBERS="upcNumbers";
    public static final String DATEFORMATTER="yyyy-MM-dd HH:mm:ss";

    public static final String RECEIVING_SHARD_KEY_FIELD = "partitionKey";

    public static final ImmutableList<Integer> PROCESS_STATUS_CODE_FOR_AUTH_FIELDS = ImmutableList.of(3, 5);

    public static final String LOCATION_TYPE_STORE="S";
    public static final String LOCATION_TYPE_WAREHOUSE="W";

    public static final String UOM_CODE_WH_EXCEPTION_RESOLUTION = "02";
    public static final String UOM_CODE_WH_MATCHING = "01";

    public final static String PAYLOAD = "payload";
    public final static String META = "meta";
    public final static Boolean TRUE = true;
    public final static String SUCCESS = "success";
    public final static String OBJECT_NAME = "domainObjectName";
    public final static String APPLICATION_TYPE_SUMMARY = "cosmosWriteSummary";
    public final static String APPLICATION_TYPE_LINE_SUMMARY = "cosmosWriteLineSummary";
    public final static String TIMESTAMP = "timestamp";
    public final static String OPERATION = "operation";
    public final static String OPERATION_TYPE = "UPDATE";

    public static final int RETRY_BACKOFF = 2000;
    public static final int RETRY_ATTEMPTS = 5;

    public final static String TIMESTAMP_TIME_ZERO = " 00:00:00";
    public final static String TIMESTAMP_23_59_59 = " 23:59:59";
    public final static String VENDOR_NUMBER_URL_PARAM = "vendorNumber";
    public final static String PO_NUMBER_URL_PARAM = "poNumber";
    public final static String RECEIPT_NUMBER_URL_PARAM = "receiptNumber";
    public final static String INVOICE_NUMBER_URL_PARAM = "invoiceNumber";
    public final static String ORIG_STORE_NUMBER_URL_PARAM = "origStoreNbr";
    public final static String PURCHASE_ORDER_ID_URL_PARAM = "purchaseOrderId";
    public final static String SCENARIO = "scenario";

}
