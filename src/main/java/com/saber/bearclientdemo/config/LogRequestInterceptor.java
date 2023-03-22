package com.saber.bearclientdemo.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.EntityDetails;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
@Slf4j
public class LogRequestInterceptor implements HttpRequestInterceptor {
    @Override
    public void process(HttpRequest request, EntityDetails entity, HttpContext context) throws HttpException, IOException {
        String requestUri = request.getRequestUri();
        String method = request.getMethod();
        String path = request.getPath();

        log.info("request for requestUri {} , path {} , method {}",requestUri,path,method);

    }
}
