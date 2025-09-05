package com.ecommerce.product_service.service;

import com.ecommerce.product_service.dto.ProductDto;

import java.util.List;

public interface IProductService {
    ProductDto createProduct(ProductDto productDto);
    ProductDto getProductById(Long id);
    List<ProductDto> getAllProducts();
    void deleteProduct(Long id);
}
