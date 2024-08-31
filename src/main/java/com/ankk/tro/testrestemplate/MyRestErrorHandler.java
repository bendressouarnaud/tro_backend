package com.ankk.tro.testrestemplate;

import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;

import java.io.IOException;

public class MyRestErrorHandler extends DefaultResponseErrorHandler {

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        super.handleError(response);
        System.out.println("MyRestErrorHandler : "+response.getStatusCode().value());
    }
}