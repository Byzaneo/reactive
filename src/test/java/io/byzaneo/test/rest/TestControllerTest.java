package io.byzaneo.test.rest;

import io.byzaneo.one.test.WithMockToken;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class TestControllerTest {

    @Autowired
    ApplicationContext context;

    private WebTestClient client;
    
    @Before
    public void setUp() {
        this.client = WebTestClient
                .bindToApplicationContext(this.context)
                .configureClient()
                .build();
    }

    @Test
    public void testInfo() {
        this.client.get()
                .uri(TestController.BASE_PATH)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Hello! I'm the test service.");
    }

    @Test
    @WithMockToken(scopes = "home")
    public void testSecure() {
        this.client.get()
                .uri(TestController.BASE_PATH + "/secure")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Hello "+WithMockToken.TESTER_EMAIL+"!");
    }
}
