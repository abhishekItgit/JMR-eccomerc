package com.ecommerce.order_service.mapper;

import com.ecommerce.order_service.dto.OrderDto;
import com.ecommerce.order_service.model.ProductOrder;

public class OrderMapper {
    public static OrderDto toDto(ProductOrder order) {
        return OrderDto.builder()
                .orderId(order.getOrderId())
                .productId(order.getProductId())
                .quantity(order.getQuantity())
                .totalPrice(order.getTotalPrice())
                .build();
    }

    public static ProductOrder toEntity(OrderDto dto) {
        return ProductOrder.builder()
                .orderId(dto.getOrderId())
                .productId(dto.getProductId())
                .quantity(dto.getQuantity())
                .totalPrice(dto.getTotalPrice())
                .build();
    }

}
