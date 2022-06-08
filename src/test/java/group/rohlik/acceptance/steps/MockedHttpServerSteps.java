package group.rohlik.acceptance.steps;

import io.cucumber.java.After;
import lombok.AllArgsConstructor;
import org.springframework.test.web.client.MockRestServiceServer;

@AllArgsConstructor
public class MockedHttpServerSteps {
    private final MockRestServiceServer server;

    @After
    public void allMockedPetitionsWereConsumed() {
        server.verify();
        server.reset();
    }
}
