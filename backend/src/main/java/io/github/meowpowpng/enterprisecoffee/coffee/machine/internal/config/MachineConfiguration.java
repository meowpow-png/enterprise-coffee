package io.github.meowpowpng.enterprisecoffee.coffee.machine.internal.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;

@Configuration
@EnableConfigurationProperties(MachineProperties.class)
public class MachineConfiguration {

    @Bean
    RestClient restClient(MachineProperties properties) {
        var httpClient = HttpClient.newBuilder()
                .connectTimeout(properties.connectTimeout())
                .build();

        var factory = new JdkClientHttpRequestFactory(httpClient);
        factory.setReadTimeout(properties.readTimeout());

        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .requestFactory(factory)
                .build();
    }
}
