package com.avis.qa.demo.client;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "test_provider", port = "1234")
public class PactConsumerTest {

    @Pact(provider="test_provider", consumer="test_consumer")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/hal+json");
        return builder
                .given("test state")
                .uponReceiving("test location interaction")
                .path("/")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body("{\"name\": \"Rajan\", \"city\" : \"Delhi\", \"companyCode\" : \"ABG100\"}")
                .toPact();
    }

    @Test
    void test(MockServer mockServer) throws Exception {
        HttpResponse httpResponse = Request.Get(mockServer.getUrl() + "/").execute().returnResponse();
        assertThat(httpResponse.getStatusLine().getStatusCode(), is(equalTo(200)));
    }
}
