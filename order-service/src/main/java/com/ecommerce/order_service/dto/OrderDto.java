package com.ecommerce.order_service.dto;

import lombok.*;


import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private Long orderId;
    private Long productId;
    private int quantity;
    private BigDecimal totalPrice;
}
