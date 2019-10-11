package com.walmart.finance.ap.fds.receiving.mesh;

import org.springframework.http.HttpHeaders;

public interface MeshHeadersGenerator {

    HttpHeaders getRequestHeaders();
}
