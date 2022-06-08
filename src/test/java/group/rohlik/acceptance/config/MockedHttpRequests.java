package group.rohlik.acceptance.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.test.web.client.ResponseCreator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MockedHttpRequests implements RequestMatcher, ResponseCreator {
    private final ObjectMapper mapper;
    private List<ClientHttpRequest> requests;
    private List<ClientHttpResponse> responses;
    private int lastResponse;

    public MockedHttpRequests(ObjectMapper objectMapper) {
        mapper = objectMapper;
        reset();
    }

    public void reset() {
        requests = new ArrayList<>();
        responses = new ArrayList<>();
        lastResponse = 0;
    }

    @Override
    public void match(ClientHttpRequest request) throws IOException, AssertionError {
        requests.add(request);
    }

    @Override
    public ClientHttpResponse createResponse(ClientHttpRequest request) throws IOException {
        return responses.get(lastResponse++);
    }

    public void addResponse(ClientHttpResponse response) {
        responses.add(response);
    }

    public void addResponse(HttpStatus status) {
        byte[] body = {};
        responses.add(new MockClientHttpResponse(body, status));
    }

    public void assertMethod(int position, HttpMethod method) {
        ClientHttpRequest request = requests.get(position);
        Assertions.assertEquals(method, request.getMethod());
    }

    public void assertUrl(int position, String url) {
        ClientHttpRequest request = requests.get(position);
        Assertions.assertEquals(url, request.getURI().toString());
    }

    public void assertHeader(int position, String header, String ...values) {
        ClientHttpRequest request = requests.get(position);
        Arrays.stream(values).forEach(value -> {
            List<String> currentValues = request.getHeaders().getValuesAsList(header);
            Assertions.assertTrue(currentValues.contains(value));
        });
    }

    public void assertBody(int position, String body) throws IOException {
        ClientHttpRequest request = requests.get(position);
        Assertions.assertEquals(mapper.readTree(body), mapper.readTree(request.getBody().toString()));
    }

    public void assertBody(int position, Object body) throws IOException {
        assertBody(position, mapper.writeValueAsString(body));
    }

    public void assertAllResponsesWereConsumed() {
        Assertions.assertEquals(lastResponse, responses.size());
    }
}
