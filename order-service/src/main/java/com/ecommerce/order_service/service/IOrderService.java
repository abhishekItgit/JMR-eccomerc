package com.ecommerce.order_service.service;

import com.ecommerce.order_service.dto.OrderDto;

public interface IOrderService {
    OrderDto createOrder(OrderDto dto);
    OrderDto getOrder(Long id);
}
