package com.between.pruebatenica.products.infrastructure;

import com.between.pruebatenica.products.domain.ProductRetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerTest {

    @LocalServerPort
    private int port;

    private WebClient webClient;

    private static final String URL_PRODUCT_SIMILAR = "/product/{productId}/similar";

    @BeforeEach
    public void setup() {
        this.webClient = WebClient.create("http://localhost:" + port);
    }

    @Test
    void getSimilarProducts1() {
        String productId = "1";

        Flux<ProductRetail> productRetailFlux = webClient.get()
                .uri(URL_PRODUCT_SIMILAR, productId)
                .retrieve()
                .bodyToFlux(ProductRetail.class);

        List<ProductRetail> products = productRetailFlux.collectList().block();
        products.sort(Comparator.comparing(product -> Integer.parseInt(product.id())));

        assertNotNull(products);
        assertFalse(products.isEmpty());

        assertEquals(3, products.size());

        assertEquals("2", products.get(0).id());
        assertEquals("Dress", products.get(0).name());
        assertEquals(19.99, products.get(0).price());
        assertTrue(products.get(0).availability());

        assertEquals("3", products.get(1).id());
        assertEquals("Blazer", products.get(1).name());
        assertEquals(29.99, products.get(1).price());
        assertFalse(products.get(1).availability());

        assertEquals("4", products.get(2).id());
        assertEquals("Boots", products.get(2).name());
        assertEquals(39.99, products.get(2).price());
        assertTrue(products.get(2).availability());
    }

    @Test
    void getSimilarProducts2() {
        String productId = "2";

        Flux<ProductRetail> productRetailFlux = webClient.get()
                .uri(URL_PRODUCT_SIMILAR, productId)
                .retrieve()
                .bodyToFlux(ProductRetail.class);

        List<ProductRetail> products = productRetailFlux.collectList().block();
        products.sort(Comparator.comparing(product -> Integer.parseInt(product.id())));

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

    @Test
    void getSimilarProducts3() {
        String productId = "3";

        Flux<ProductRetail> productRetailFlux = webClient.get()
                .uri(URL_PRODUCT_SIMILAR, productId)
                .retrieve()
                .bodyToFlux(ProductRetail.class);

        List<ProductRetail> products = productRetailFlux.collectList().block();
        products.sort(Comparator.comparing(product -> Integer.parseInt(product.id())));

        assertNotNull(products);
        assertFalse(products.isEmpty());

        assertEquals(3, products.size());

        assertEquals("100", products.get(0).id());
        assertEquals("Trousers", products.get(0).name());
        assertEquals(49.99, products.get(0).price());
        assertFalse(products.get(0).availability());

        assertEquals("1000", products.get(1).id());
        assertEquals("Coat", products.get(1).name());
        assertEquals(89.99, products.get(1).price());
        assertTrue(products.get(1).availability());

        assertEquals("10000", products.get(2).id());
        assertEquals("Leather jacket", products.get(2).name());
        assertEquals(89.99, products.get(2).price());
        assertTrue(products.get(2).availability());

    }

    @Test
    void getSimilarProducts4() {
        String productId = "4";

        Flux<ProductRetail> productRetailFlux = webClient.get()
                .uri(URL_PRODUCT_SIMILAR, productId)
                .retrieve()
                .bodyToFlux(ProductRetail.class);

        List<ProductRetail> products = productRetailFlux.collectList().block();
        products.sort(Comparator.comparing(product -> Integer.parseInt(product.id())));

        assertNotNull(products);
        assertFalse(products.isEmpty());

        assertEquals(2, products.size());

        assertEquals("1", products.get(0).id());
        assertEquals("Shirt", products.get(0).name());
        assertEquals(9.99, products.get(0).price());
        assertTrue(products.get(0).availability());

        assertEquals("2", products.get(1).id());
        assertEquals("Dress", products.get(1).name());
        assertEquals(19.99, products.get(1).price());
        assertTrue(products.get(1).availability());
    }

    @Test
    void getSimilarProducts5() {
        String productId = "5";

        Flux<ProductRetail> productRetailFlux = webClient.get()
                .uri(URL_PRODUCT_SIMILAR, productId)
                .retrieve()
                .bodyToFlux(ProductRetail.class);

        List<ProductRetail> products = productRetailFlux.collectList().block();
        products.sort(Comparator.comparing(product -> Integer.parseInt(product.id())));

        assertNotNull(products);
        assertFalse(products.isEmpty());

        assertEquals(2, products.size());

        assertEquals("1", products.get(0).id());
        assertEquals("Shirt", products.get(0).name());
        assertEquals(9.99, products.get(0).price());
        assertTrue(products.get(0).availability());

        assertEquals("2", products.get(1).id());
        assertEquals("Dress", products.get(1).name());
        assertEquals(19.99, products.get(1).price());
        assertTrue(products.get(1).availability());
    }

}