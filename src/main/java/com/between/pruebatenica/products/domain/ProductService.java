package com.between.pruebatenica.products.domain;

import reactor.core.publisher.Flux;

public interface ProductService {

    Flux<ProductRetail> getSimilarProducts(String productId);

}
