package com.example.Integration_testing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class IntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Container
    static GenericContainer<?> devContainer = new GenericContainer<>(DockerImageName.parse("devapp:latest"))
            .withExposedPorts(8080)
            .waitingFor(Wait.forHttp("/actuator/health")
                    .forStatusCode(200)
                    .withStartupTimeout(Duration.ofSeconds(120)));  // Изменено время ожидания

    @Container
    static GenericContainer<?> prodContainer = new GenericContainer<>(DockerImageName.parse("prodapp:latest"))
            .withExposedPorts(8081)
            .waitingFor(Wait.forHttp("/actuator/health")
                    .forStatusCode(200)
                    .withStartupTimeout(Duration.ofSeconds(120)));  // Изменено время ожидания

    @Test
    void testDevEnvironment() {
        String url = "http://localhost:" + devContainer.getMappedPort(8080) +
                "/authorize?user=admin&password=password";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        System.out.println("Dev response status: " + response.getStatusCodeValue());
        System.out.println("Dev response body: " + response.getBody());

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("[\"READ\",\"WRITE\",\"DELETE\"]", response.getBody());
    }

    @Test
    void testProdEnvironment() {
        String url = "http://localhost:" + prodContainer.getMappedPort(8081) +
                "/authorize?user=admin&password=password";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        System.out.println("Prod response status: " + response.getStatusCodeValue());
        System.out.println("Prod response body: " + response.getBody());

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("[\"READ\",\"WRITE\",\"DELETE\"]", response.getBody());
    }
}