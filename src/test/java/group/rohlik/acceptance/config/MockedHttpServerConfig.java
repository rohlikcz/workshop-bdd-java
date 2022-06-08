package group.rohlik.acceptance.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@AllArgsConstructor
public class MockedHttpServerConfig {
    private final RestTemplate restTemplate;

    @Bean
    public MockedHttpRequests mockServerRequests(ObjectMapper mapper) {
        return new MockedHttpRequests(mapper);
    }
}
