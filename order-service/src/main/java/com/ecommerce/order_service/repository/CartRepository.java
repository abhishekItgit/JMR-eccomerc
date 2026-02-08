package com.ecommerce.order_service.repository;

import com.ecommerce.order_service.model.Cart;
import com.ecommerce.order_service.model.CartStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByCustomerIdAndStatus(Long customerId, CartStatus status);
}
