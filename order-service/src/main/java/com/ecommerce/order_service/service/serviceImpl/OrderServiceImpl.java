package com.ecommerce.order_service.service.serviceImpl;




import com.ecommerce.order_service.client.ProductClient;
import com.ecommerce.order_service.dto.OrderDto;
import com.ecommerce.order_service.dto.ProductDto;
import com.ecommerce.order_service.mapper.OrderMapper;
import com.ecommerce.order_service.model.ProductOrder;
import com.ecommerce.order_service.repository.IOrderRepository;
import com.ecommerce.order_service.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final IOrderRepository orderRepository;
    private final ProductClient productClient;

    @Override
    public OrderDto createOrder(OrderDto dto) {
        ProductOrder order = OrderMapper.toEntity(dto);
        order.setOrderDate(LocalDateTime.now());

        // 💡 later we’ll add product-service call here
        ProductOrder saved = orderRepository.save(order);
        return OrderMapper.toDto(saved);
    }

    @Override
    public OrderDto getOrder(Long id) {
        return orderRepository.findById(id)
                .map(OrderMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }

    public ProductOrder placeOrder(Long productID, int quantity ) throws Exception {
        ProductDto productResponse = Optional.ofNullable(productClient.getProductById(productID))
                .map(ResponseEntity :: getBody).orElseThrow(()-> new Exception("Product Not Found"+ productID));

        if (productResponse.getProductId()== null){
           throw new Exception ("no product in stock" + productID);
        }

        ProductOrder productOrder =  ProductOrder.builder().productId(productResponse.getProductId())
                        .orderDate(LocalDateTime.now()).productId(productResponse.getProductId())
                       .quantity(7)
                .totalPrice(productResponse.getPrice())
                 .build();

     return productOrder;
    }
}

