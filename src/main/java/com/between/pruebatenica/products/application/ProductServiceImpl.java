package com.between.pruebatenica.products.application;

import com.between.pruebatenica.config.cache.CacheConfig;
import com.between.pruebatenica.products.domain.ProductRetail;
import com.between.pruebatenica.products.domain.ProductService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service("productServiceImpl")
public class ProductServiceImpl implements ProductService {

    private final WebClient webClient;

    public ProductServiceImpl(WebClient.Builder webClientBuilder, @Value("${external.product-service.base-url:5000}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    /**
     * Retrieves a list of products similar to the given product ID.
     *
     * This method fetches similar product IDs from an external service,
     * retrieves the product details for each ID, and filters out null or invalid products.
     * It utilizes caching to store the results and a circuit breaker to handle failures gracefully.
     *
     * @param productId The ID of the product for which similar products are to be retrieved.
     * @return A Flux of ProductRetail containing similar products.
     */
    @Override
    @Cacheable(value = CacheConfig.USERS_INFO_CACHE, key = "#productId", unless = "#result == null")
    @CircuitBreaker(name = "similarProducts", fallbackMethod = "fallbackSimilarProducts")
    public Flux<ProductRetail> getSimilarProducts(String productId) {
        return this.webClient.get()
                .uri("/product/{productId}/similarids", productId)
                .retrieve()
                .bodyToFlux(String.class)
                .flatMap(this::getProductDetailsByProductId)
                .filter(product -> product != null && product.id() != null)
                .onErrorResume(e -> Flux.empty());
    }

    private Flux<ProductRetail> fallbackSimilarProducts() {
        return Flux.empty();
    }

    private Flux<ProductRetail> getProductDetailsByProductId(String productIds) {
        if (isNullOrBlank(productIds)) {
            return Flux.empty();
        }
        var productIdsArray = parseProductIds(productIds);
        return getProductDetails(productIdsArray);
    }

    private boolean isNullOrBlank(String productIds) {
        return productIds == null || productIds.isBlank() || productIds.equals("[]");
    }

    private String[] parseProductIds(String productIds) {
        var cleanedProductIds = productIds.replaceAll("[\\[\\]]", "");
        return cleanedProductIds.split(",");
    }

    private Flux<ProductRetail> getProductDetails(String[] productIdsArray) {
        return Flux.fromArray(productIdsArray)
                .flatMap(this::fetchProductDetails);
    }

    /**
     * Fetches the product details for a given product ID.
     *
     * This method sends a GET request to the external product service to
     * retrieve the details of the product specified by the product ID.
     * If the product is not found, an empty Mono is returned.
     * If a server error occurs, an exception is thrown.
     *
     * @param productId The ID of the product to be fetched.
     * @return A Flux of ProductRetail containing the product details.
     */
    private Flux<ProductRetail> fetchProductDetails(String productId) {
        return this.webClient.get()
                .uri("/product/{productId}", productId)
                .retrieve()
                .onStatus(status -> status.isSameCodeAs(HttpStatus.NOT_FOUND),
                        response -> Mono.empty())
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new RuntimeException("Server error for product: " + productId)))
                .bodyToFlux(ProductRetail.class);
    }

}
