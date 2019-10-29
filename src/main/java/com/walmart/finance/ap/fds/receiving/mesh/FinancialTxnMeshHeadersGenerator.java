package com.walmart.finance.ap.fds.receiving.mesh;

import com.walmart.finance.ap.fds.receiving.common.ReceivingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import java.security.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Component
public class FinancialTxnMeshHeadersGenerator implements MeshHeadersGenerator {

    public static final Logger log = LoggerFactory.getLogger(FinancialTxnMeshHeadersGenerator.class);

    @Value("${mesh.consumerId}")
    private String consumerId;

    @Value("${mesh.producerHostName}")
    private String producerHostName;

    @Value("${financialTxn.appName}")
    private String appName;

    @Value("${financialTxn.appEnv}")
    private String appEnv;

    @Value("${mesh.consumerKeyVersion}")
    private String consumerKeyVersion;

    @Value("${receiving-mesh-consumer-private-key}")
    private String consumerPrivateKey;

    @Value(("${mesh.useHostHeader}"))
    private Boolean useHostHeader;

    @Override
    public HttpHeaders getRequestHeaders() {
        log.info("Started generating headers at " + LocalDateTime.now());
        HttpHeaders requestHeaders = new HttpHeaders();

        Long invocationTs = System.currentTimeMillis();
        requestHeaders.set(ReceivingConstants.SM_WM_CONSUMER, consumerId);
        requestHeaders.set(ReceivingConstants.SM_WM_APP_NAME, appName);
        requestHeaders.set(ReceivingConstants.SM_WM_ENV, appEnv);
        if(useHostHeader) {
            requestHeaders.set("Host", producerHostName); // This is to override host as localhost:4140
        }
//        requestHeaders.set(ReceivingConstants.SM_WM_KEY_VERSION, consumerKeyVersion);
//        requestHeaders.set(ReceivingConstants.SM_INVOCATION_TS, invocationTs.toString());
//        requestHeaders.set(ReceivingConstants.SM_AUTH_SIGN, getSignature(invocationTs.toString()));
        log.info("Completed generating headers at " + LocalDateTime.now());
        return requestHeaders;
    }

    /**
     * String to sign is combination of following fields (obtained via canonicalization)
     * 1. Consumer ID
     * 2. Invocation Timestamp
     * 3. Consumer Key Version
     * Separated by \n character
     */
    private String getSignature(String invocationTs) {
        Map<String, String> map = new HashMap<>();
        map.put(ReceivingConstants.SM_WM_CONSUMER, consumerId);
        map.put(ReceivingConstants.SM_INVOCATION_TS, invocationTs);
        map.put(ReceivingConstants.SM_WM_KEY_VERSION, consumerKeyVersion);
        String[] array = canonicalize(map);
        return generateSignature(array[1]);
    }

    private String generateSignature(String data) {
        try {
            log.info("setting signature instance: " + LocalDateTime.now());
            Signature signatureInstance = Signature.getInstance("SHA256WithRSA");

            ServiceKeyRep keyRep = new ServiceKeyRep(KeyRep.Type.PRIVATE, "RSA", "PKCS#8",
                    Base64.getDecoder().decode(consumerPrivateKey));

            PrivateKey resolvedPrivateKey = (PrivateKey) keyRep.readResolve();

            signatureInstance.initSign(resolvedPrivateKey);

            log.info("Starting to sign : " + LocalDateTime.now());
            byte[] bytesToSign = data.getBytes("UTF-8");
            signatureInstance.update(bytesToSign);
            byte[] signatureBytes = signatureInstance.sign();
            String string = Base64.getEncoder().encodeToString(signatureBytes);
            log.info("Completed encoding of signature: " + LocalDateTime.now());
            return string;
        } catch (InvalidKeyException ex) {
            log.error("Invalid Consumer Private Key \n" + ex.getMessage());
        } catch (Exception ex) {
            log.error("Exception occurred while generating signature for Data: \n" + data +
                    "\n" + ex.getMessage());
        }
        return null;
    }

    protected static String[] canonicalize(Map<String, String> headersToSign) {
        StringBuffer canonicalizedStrBuffer = new StringBuffer();
        StringBuffer parameterNamesBuffer = new StringBuffer();
        Set<String> keySet = headersToSign.keySet();

        // Create sorted key set to enforce order on the key names
        SortedSet<String> sortedKeySet = new TreeSet<String>(keySet);
        for (String key : sortedKeySet) {
            Object val = headersToSign.get(key);
            parameterNamesBuffer.append(key.trim()).append(";");
            canonicalizedStrBuffer.append(val.toString().trim()).append("\n");
        }

        return new String[]{parameterNamesBuffer.toString(), canonicalizedStrBuffer.toString()};
    }

}