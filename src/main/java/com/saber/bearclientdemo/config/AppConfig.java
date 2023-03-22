package com.saber.bearclientdemo.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.camel.CamelContext;
import org.apache.camel.component.metrics.routepolicy.MetricsRoutePolicyFactory;
import org.apache.camel.component.micrometer.messagehistory.MicrometerMessageHistoryFactory;
import org.apache.camel.component.micrometer.routepolicy.MicrometerRoutePolicyFactory;
import org.apache.camel.spring.boot.CamelContextConfiguration;
import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.SocketConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class AppConfig {


    @Value(value = "${spring.restTemplate.connectionPerRoute}")
    private Integer connectionPerRoute;
    @Value(value = "${spring.restTemplate.maxTotalConnection}")
    private Integer maxTotalConnection;
    @Value(value = "${spring.restTemplate.readTimeout}")
    private Integer readTimeout;
    @Value(value = "${spring.restTemplate.connectTimeout}")
    private Integer connectTimeout;

    @Autowired
    private LogRequestInterceptor logRequestInterceptor;

    @Bean
    public ObjectMapper mapper() {
        return new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .configure(SerializationFeature.FAIL_ON_SELF_REFERENCES, false)
                .configure(SerializationFeature.FAIL_ON_UNWRAPPED_TYPE_IDENTIFIERS, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    @Bean
    public CamelContextConfiguration camelContextConfiguration() {
        return new CamelContextConfiguration() {
            @Override
            public void beforeApplicationStart(CamelContext camelContext) {
                camelContext.addRoutePolicyFactory(new MicrometerRoutePolicyFactory());
                camelContext.addRoutePolicyFactory(new MetricsRoutePolicyFactory());
                camelContext.setMessageHistoryFactory(new MicrometerMessageHistoryFactory());
            }

            @Override
            public void afterApplicationStart(CamelContext camelContext) {
            }
        };
    }


    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        cors.setAllowedOrigins(List.of("*"));
        cors.setAllowedMethods(List.of("*"));
        cors.setAllowedHeaders(List.of("*"));
        cors.addAllowedOriginPattern("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return source;
    }

    @Bean
    public RestTemplate restTemplate() throws Exception {

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        HttpRoute httpRoute = new HttpRoute(new HttpHost("http", "/api.springframework.guru", 80));
        connectionManager.setMaxPerRoute(httpRoute, connectionPerRoute);
        connectionManager.setMaxTotal(maxTotalConnection);
        SocketConfig socketConfig = SocketConfig.custom()
                .setSoTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .build();

        connectionManager.setDefaultSocketConfig(socketConfig);

        HttpClient httpClient = HttpClientBuilder.create()
                 .build();

        RestTemplate restTemplate = new RestTemplate();

        HttpComponentsClientHttpRequestFactory httpComponent =
                new HttpComponentsClientHttpRequestFactory();
        httpComponent.setConnectionRequestTimeout(readTimeout);
        httpComponent.setConnectTimeout(connectTimeout);
        httpComponent.setHttpClient(httpClient);

         restTemplate.setRequestFactory(httpComponent);
         restTemplate.getInterceptors().add(new LoggingRequestInterceptor());
        return restTemplate;
    }
}
