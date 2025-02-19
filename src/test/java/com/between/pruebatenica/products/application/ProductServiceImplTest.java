package com.between.pruebatenica.products.application;

import com.between.pruebatenica.products.domain.ProductRetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductServiceImplTest {

    @LocalServerPort
    private int port;

    private WebClient webClient;

    private static final String URL_PRODUCT_SIMILAR = "/product/{productId}/similar";

    @BeforeEach
    public void setup() {
        webClient = WebClient.create("http://localhost:" + port);
    }

    @Test
    void testGetSimilarProducts1() {
        String productId = "1";

        Flux<ProductRetail> productRetailFlux = webClient.get()
                .uri(URL_PRODUCT_SIMILAR, productId)
                .retrieve()
                .bodyToFlux(ProductRetail.class);

        List<ProductRetail> products = productRetailFlux.collectList().block();

        assertNotNull(products);
        assertFalse(products.isEmpty());

        assertEquals(3, products.size());

        assertEquals("2", products.get(0).id());
        assertEquals("Dress", products.get(0).name());
        assertEquals(19.99, products.get(0).price());
        assertTrue(products.get(0).availability());

        assertEquals("4", products.get(1).id());
        assertEquals("Boots", products.get(1).name());
        assertEquals(39.99, products.get(1).price());
        assertTrue(products.get(1).availability());

        assertEquals("3", products.get(2).id());
        assertEquals("Blazer", products.get(2).name());
        assertEquals(29.99, products.get(2).price());
        assertFalse(products.get(2).availability());
    }

    @Test
    void testGetSimilarProducts2() {
        String productId = "2";

        Flux<ProductRetail> productRetailFlux = webClient.get()
                .uri(URL_PRODUCT_SIMILAR, productId)
                .retrieve()
                .bodyToFlux(ProductRetail.class);

        List<ProductRetail> products = productRetailFlux.collectList().block();

        assertNotNull(products);
        assertFalse(products.isEmpty());

        assertEquals(3, products.size());

        assertEquals("3", products.get(0).id());
        assertEquals("Blazer", products.get(0).name());
        assertEquals(29.99, products.get(0).price());
        assertFalse(products.get(0).availability());

        assertEquals("100", products.get(1).id());
        assertEquals("Trousers", products.get(1).name());
        assertEquals(49.99, products.get(1).price());
        assertFalse(products.get(1).availability());

        assertEquals("1000", products.get(2).id());
        assertEquals("Coat", products.get(2).name());
        assertEquals(89.99, products.get(2).price());
        assertTrue(products.get(2).availability());
    }

}