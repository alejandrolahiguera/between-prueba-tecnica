package com.between.pruebatenica.products.application;

import com.between.pruebatenica.config.cache.CacheConfig;
import com.between.pruebatenica.products.domain.ProductRetail;
import com.between.pruebatenica.products.domain.ProductService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service("productServiceImpl")
public class ProductServiceImpl implements ProductService {

    private final WebClient webClient;

    public ProductServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:3001").build();
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
    @CircuitBreaker(name = "getSimilarProductsCircuitBreaker", fallbackMethod = "getSimilarProductsFallback")
    public Flux<ProductRetail> getSimilarProducts(String productId) {
        return this.webClient.get()
                .uri("/product/{productId}/similarids", productId)
                .retrieve()
                .bodyToFlux(String.class)
                .flatMap(this::getProductDetails)
                .filter(product -> product != null && product.id() != null);
    }

    private Flux<ProductRetail> getSimilarProductsFallback(String productId, Throwable throwable) {
        System.out.println("ha entrado");
        return Flux.empty();
    }

    /**
     * Given a string of product ids (comma or bracket separated),
     * returns a flux of products for those ids.
     * If the string is empty or null, returns an empty flux.
     * If the string contains a single id, calls {@link #getSingleProductDetail(String)},
     * if it contains multiple ids, calls {@link #getMultipleProductDetails(String[])}.
     *
     * @param productIds the string of product ids
     * @return a flux of products for the given ids
     */
    private Flux<ProductRetail> getProductDetails(String productIds) {
        if (isNullOrBlank(productIds)) {
            return Flux.empty();
        }

        String[] productIdsArray = parseProductIds(productIds);

        if (productIdsArray.length > 1) {
            return getMultipleProductDetails(productIdsArray);
        }

        return getSingleProductDetail(productIdsArray[0]);
    }

    private boolean isNullOrBlank(String productIds) {
        return productIds == null || productIds.isBlank();
    }

    private String[] parseProductIds(String productIds) {
        String cleanedProductIds = productIds.replaceAll("[\\[\\]]", "");
        return cleanedProductIds.split(",");
    }

    private Flux<ProductRetail> getMultipleProductDetails(String[] productIdsArray) {
        return Flux.fromArray(productIdsArray)
                .flatMap(this::fetchProductDetails);
    }

    private Flux<ProductRetail> getSingleProductDetail(String productId) {
        return this.webClient.get()
                .uri("/product/{productId}", productId)
                .retrieve()
                .bodyToMono(ProductRetail.class)
                .flux();
    }

    private Flux<ProductRetail> fetchProductDetails(String productId) {
        return this.webClient.get()
                .uri("/product/{productId}", productId)
                .retrieve()
                .onStatus(httpStatusCode -> httpStatusCode.is4xxClientError(),
                        response -> Mono.empty())
                .onStatus(httpStatusCode -> httpStatusCode.is5xxServerError(),
                        response -> Mono.empty())
                .bodyToFlux(ProductRetail.class);
    }

}
