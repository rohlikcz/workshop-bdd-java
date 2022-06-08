package group.rohlik.acceptance.steps;

import group.rohlik.acceptance.config.MockedHttpRequests;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

public class MockedHttpServerSteps {
    private final MockedHttpRequests mockRequests;
    private final MockRestServiceServer server;

    public MockedHttpServerSteps(RestTemplate restTemplate, MockedHttpRequests mockRequests) {
        this.mockRequests = mockRequests;
        this.server = MockRestServiceServer.bindTo(restTemplate).build();
    }

    @Before
    public void setUp() {
        server.reset();
        mockRequests.reset();
        server
                .expect(mockRequests)
                .andRespond(mockRequests);
    }

    @After
    public void allMockedPetitionsWereConsumed() {
        mockRequests.assertAllResponsesWereConsumed();
    }
}
