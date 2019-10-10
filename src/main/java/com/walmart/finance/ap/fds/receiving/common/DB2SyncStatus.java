package com.walmart.finance.ap.fds.receiving.common;

public  enum DB2SyncStatus {

    INSERT_SYNC_INITIATED("Insert Sync Initiated"),
    INSERT_SYNC_COMPLATED ("Insert Sync completed - DB2"),
    INSERT_SYNC_FAILED("Insert Sync failed - DB2"),
    UPDATE_SYNC_INITIATED( "Update Sync Initiated - DB2"),
    UPDATE_SYNC_COMPLATED  ("Update Sync completed - DB2"),
    UPDATE_SYNC_FAILED ("Update Sync failed - DB2");

    private String value;

    DB2SyncStatus(String value)
    {
        this.value = value;
    }
}