package com.ecommerce.product_service.service;

import com.ecommerce.product_service.dto.ProductRequest;
import com.ecommerce.product_service.dto.ProductResponse;
import com.ecommerce.product_service.model.Product;
import com.ecommerce.product_service.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        applyRequest(product, request, true);
        return toResponse(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> list() {
        return productRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return productRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> search(String query) {
        if (query == null || query.isBlank()) {
            return list();
        }
        return productRepository.search(query).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        applyRequest(product, request, false);
        return toResponse(productRepository.save(product));
    }

    @Transactional
    public void delete(Long id) {
        Optional<Product> existing = productRepository.findById(id);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Product not found");
        }
        productRepository.deleteById(id);
    }

    private void applyRequest(Product product, ProductRequest request, boolean required) {
        if (required || request.getName() != null) {
            product.setName(request.getName());
        }
        if (required || request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (required || request.getBrand() != null) {
            product.setBrand(request.getBrand());
        }
        if (required || request.getCategory() != null) {
            product.setCategory(request.getCategory());
        }
        if (required || request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (required || request.getQuantity() != null) {
            product.setQuantity(request.getQuantity());
        }
        if (required || request.getSize() != null) {
            product.setSize(request.getSize());
        }
        if (required || request.getColor() != null) {
            product.setColor(request.getColor());
        }
        if (required || request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }
    }

    private ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setBrand(product.getBrand());
        response.setCategory(product.getCategory());
        response.setPrice(product.getPrice());
        response.setQuantity(product.getQuantity());
        response.setSize(product.getSize());
        response.setColor(product.getColor());
        response.setImageUrl(product.getImageUrl());
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        return response;
    }
}
