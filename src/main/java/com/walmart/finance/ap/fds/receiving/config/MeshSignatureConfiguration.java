package com.walmart.finance.ap.fds.receiving.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MeshSignatureConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeshSignatureConfiguration.class);

    @Value("${receiving-mesh-consumer-private-key}")
    private String privatekey;

    @Value("${mesh.consumerId}")
    private String consumerId;

    @Value("${mesh.consumerKeyVersion}")
    private String keyVersion;

    @Value("${SR_SIGNATURE_PATH:/secrets}")
    private String signaurePath;

    @PostConstruct
    public void init() throws IOException {
        Path path = Paths.get(signaurePath + "/signature.properties");
        if (Files.exists(path)) {
            StringBuffer content = new StringBuffer("privateKey.")
                    .append(consumerId)
                    .append("=")
                    .append(privatekey)
                    .append("\n")
                    .append("keyVersion.")
                    .append(consumerId)
                    .append("=")
                    .append(keyVersion);
            Files.write(path, content.toString().getBytes());
            LOGGER.info("Written signature info to Mesh Secret File:{}!", path);
        } else {
            LOGGER.error("Mesh Secret File:{} doesn't exist to write the information!", path);
        }
    }

}