package group.rohlik.acceptance.steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;

public class HttpSteps {
    private final TestRestTemplate template;
    private final HttpHeaders headers;
    private ResponseEntity<String> response;

    @Autowired
    public HttpSteps(TestRestTemplate template)
    {
        this.template = template;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        setUp();
    }

    @Before
    public void setUp() {
        response = null;
    }

    @When("I send a {string} request to {string}")
    public void sendARequestWithBodyToWith(String method, String path) {
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        response = template.exchange(path, HttpMethod.resolve(method.toUpperCase()), entity, String.class);
    }

    @Then("the response status should be {int} with body:")
    public void theResponseStatusAndBodyShouldBe(int status, String body) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.resolve(status));
        assertThat(mapper.readTree(response.getBody())).isEqualTo(mapper.readTree(body));
    }
}
