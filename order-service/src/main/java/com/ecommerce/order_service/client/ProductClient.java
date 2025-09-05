package com.ecommerce.order_service.client;

import com.ecommerce.order_service.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


// Communicates with Product Service registered on Eureka as "product-service"
@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ResponseEntity<ProductDto> getProductById(@PathVariable Long id);
}
