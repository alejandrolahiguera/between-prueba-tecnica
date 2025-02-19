package com.between.pruebatenica.products.infrastructure;

import com.between.pruebatenica.products.domain.ProductRetail;
import com.between.pruebatenica.products.domain.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{productId}/similar")
    public Flux<ProductRetail> getSimilarProducts(@PathVariable String productId) {
        return this.productService.getSimilarProducts(productId);
    }

}

