package com.between.pruebatenica.products.application;

import com.between.pruebatenica.config.cache.CacheConfig;
import com.between.pruebatenica.products.domain.ProductNotFoundException;
import com.between.pruebatenica.products.domain.ProductRetail;
import com.between.pruebatenica.products.domain.ProductService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service("productServiceImpl")
public class ProductServiceImpl implements ProductService {

    private final WebClient webClient;

    public ProductServiceImpl(WebClient.Builder webClientBuilder, @Value("${external.product-service.base-url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    /**
     * Retrieves a flux of products similar to the one with the given id.
     * The flux is first cached and then wrapped in a circuit breaker.
     * If the circuit breaker is open, the method will return a flux containing
     * no elements.
     *
     * @param productId the id of the product for which to retrieve similar items
     * @return a flux of products similar to the one with the given id
     */
    @Override
    @Cacheable(value = CacheConfig.USERS_INFO_CACHE, unless = "#result == null")
    @CircuitBreaker(name = "similarProductsCircuitBreaker", fallbackMethod = "getSimilarProductsFallback")
    public Flux<ProductRetail> getSimilarProducts(String productId) {
        return this.webClient.get()
                .uri("/product/{productId}/similarids", productId)
                .retrieve()
                .bodyToFlux(String.class)
                .flatMap(this::getProductDetailsByProductId)
                .filter(product -> product != null && product.id() != null);
    }

    private Flux<ProductRetail> getSimilarProductsFallback(String productId, Throwable throwable) {
        throw new ProductNotFoundException(
                "Failed to retrieve similar products for productId: " + productId + ". Reason: " + throwable.getMessage(),
                throwable
        );
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

    private Flux<ProductRetail> fetchProductDetails(String productId) {
        return this.webClient.get()
                .uri("/product/{productId}", productId)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.empty())
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.empty())
                .bodyToFlux(ProductRetail.class);
    }

}
