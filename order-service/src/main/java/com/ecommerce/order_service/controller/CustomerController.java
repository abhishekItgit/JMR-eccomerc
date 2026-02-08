package com.ecommerce.order_service.controller;

import com.ecommerce.order_service.dto.AddressRequest;
import com.ecommerce.order_service.dto.AddressResponse;
import com.ecommerce.order_service.dto.CustomerRequest;
import com.ecommerce.order_service.dto.CustomerResponse;
import com.ecommerce.order_service.model.Customer;
import com.ecommerce.order_service.service.EcommerceOrderService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final EcommerceOrderService orderService;

    public CustomerController(EcommerceOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerRequest request) {
        Customer customer = orderService.createCustomer(request);
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setName(customer.getName());
        response.setEmail(customer.getEmail());
        response.setCreatedAt(customer.getCreatedAt());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{customerId}/addresses")
    public ResponseEntity<AddressResponse> addAddress(
            @PathVariable Long customerId,
            @RequestBody AddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.addAddress(customerId, request));
    }

    @GetMapping("/{customerId}/addresses")
    public ResponseEntity<List<AddressResponse>> listAddresses(@PathVariable Long customerId) {
        return ResponseEntity.ok(orderService.getAddresses(customerId));
    }
}
