package root.authorithationservicestoryline.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {
    @Value("${user-management-service.port}")
    private String USER_MANAGEMENT_SERVICE_PORT;

    @Value("${user-management-service.link}")
    private String USER_MANAGEMENT_SERVICE_LINK;

    @Bean
    public WebClient webClient(WebClient.Builder webClientBuilder) {
        String baseUrl = USER_MANAGEMENT_SERVICE_LINK + USER_MANAGEMENT_SERVICE_PORT;
        return webClientBuilder.baseUrl(baseUrl).build();
    }
}
