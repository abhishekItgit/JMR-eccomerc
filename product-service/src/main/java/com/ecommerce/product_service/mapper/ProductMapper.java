package com.ecommerce.product_service.mapper;

import com.ecommerce.product_service.dto.ProductDto;
import com.ecommerce.product_service.entity.Product;

public class ProductMapper {
    public static ProductDto toDto(Product product) {
        return ProductDto.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .price(product.getPrice())
                .build();
    }

    public static Product toEntity(ProductDto dto) {
        return Product.builder()
                .productId(dto.getProductId())
                .name(dto.getName())
                .price(dto.getPrice())
                .build();
    }
}
