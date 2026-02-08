package com.ecommerce.order_service.controller;

import com.ecommerce.order_service.dto.CartItemRequest;
import com.ecommerce.order_service.dto.CartItemUpdateRequest;
import com.ecommerce.order_service.dto.CartResponse;
import com.ecommerce.order_service.service.EcommerceOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final EcommerceOrderService orderService;

    public CartController(EcommerceOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{customerId}/items")
    public ResponseEntity<CartResponse> addItem(
            @PathVariable Long customerId,
            @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(orderService.addCartItem(customerId, request));
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getCart(customerId));
    }

    @PatchMapping("/{customerId}/items/{itemId}")
    public ResponseEntity<CartResponse> updateItem(
            @PathVariable Long customerId,
            @PathVariable Long itemId,
            @RequestBody CartItemUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateCartItem(customerId, itemId, request));
    }

    @DeleteMapping("/{customerId}/items/{itemId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable Long customerId,
            @PathVariable Long itemId) {
        orderService.removeCartItem(customerId, itemId);
        return ResponseEntity.noContent().build();
    }
}
