package group.rohlik.acceptance.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@Configuration
@AllArgsConstructor
public class MockedHttpServerConfig {
    private final RestTemplate restTemplate;

    @Bean
    public MockRestServiceServer mockRestServiceServer() {
        return MockRestServiceServer.bindTo(restTemplate).build();
    }
}
