package com.ecommerce.order_service.repository;

import com.ecommerce.order_service.model.ProductOrder;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IOrderRepository extends JpaRepository<ProductOrder,Long> {
}
