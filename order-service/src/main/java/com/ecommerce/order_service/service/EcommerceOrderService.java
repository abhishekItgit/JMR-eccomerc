package com.ecommerce.order_service.service;

import com.ecommerce.order_service.dto.AddressRequest;
import com.ecommerce.order_service.dto.AddressResponse;
import com.ecommerce.order_service.dto.CartItemRequest;
import com.ecommerce.order_service.dto.CartItemResponse;
import com.ecommerce.order_service.dto.CartItemUpdateRequest;
import com.ecommerce.order_service.dto.CartResponse;
import com.ecommerce.order_service.dto.CheckoutRequest;
import com.ecommerce.order_service.dto.CustomerRequest;
import com.ecommerce.order_service.dto.OrderItemResponse;
import com.ecommerce.order_service.dto.OrderResponse;
import com.ecommerce.order_service.dto.PaymentResponse;
import com.ecommerce.order_service.model.Address;
import com.ecommerce.order_service.model.Cart;
import com.ecommerce.order_service.model.CartItem;
import com.ecommerce.order_service.model.CartStatus;
import com.ecommerce.order_service.model.Customer;
import com.ecommerce.order_service.model.CustomerOrder;
import com.ecommerce.order_service.model.OrderItem;
import com.ecommerce.order_service.model.OrderStatus;
import com.ecommerce.order_service.model.Payment;
import com.ecommerce.order_service.model.PaymentStatus;
import com.ecommerce.order_service.repository.AddressRepository;
import com.ecommerce.order_service.repository.CartItemRepository;
import com.ecommerce.order_service.repository.CartRepository;
import com.ecommerce.order_service.repository.CustomerOrderRepository;
import com.ecommerce.order_service.repository.CustomerRepository;
import com.ecommerce.order_service.repository.PaymentRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EcommerceOrderService {

    private final CustomerRepository customerRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerOrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public EcommerceOrderService(
            CustomerRepository customerRepository,
            AddressRepository addressRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            CustomerOrderRepository orderRepository,
            PaymentRepository paymentRepository) {
        this.customerRepository = customerRepository;
        this.addressRepository = addressRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public Customer createCustomer(CustomerRequest request) {
        Optional<Customer> existing = customerRepository.findByEmail(request.getEmail());
        if (existing.isPresent()) {
            return existing.get();
        }
        Customer customer = new Customer();
        customer.setName(request.getName());
        customer.setEmail(request.getEmail());
        return customerRepository.save(customer);
    }

    @Transactional
    public AddressResponse addAddress(Long customerId, AddressRequest request) {
        Customer customer = getCustomer(customerId);
        if (request.isDefaultAddress()) {
            List<Address> existing = addressRepository.findByCustomerId(customerId);
            existing.forEach(address -> address.setDefaultAddress(false));
            addressRepository.saveAll(existing);
        }
        Address address = new Address();
        address.setCustomer(customer);
        address.setLine1(request.getLine1());
        address.setLine2(request.getLine2());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        address.setDefaultAddress(request.isDefaultAddress());
        return toResponse(addressRepository.save(address));
    }

    @Transactional(readOnly = true)
    public List<AddressResponse> getAddresses(Long customerId) {
        return addressRepository.findByCustomerId(customerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CartResponse addCartItem(Long customerId, CartItemRequest request) {
        Cart cart = getOrCreateCart(customerId);
        Optional<CartItem> existing = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(request.getProductId()))
                .findFirst();
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            cartItemRepository.save(item);
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProductId(request.getProductId());
            item.setProductName(request.getProductName());
            item.setUnitPrice(request.getUnitPrice());
            item.setQuantity(request.getQuantity());
            cart.getItems().add(cartItemRepository.save(item));
        }
        return toCartResponse(cartRepository.save(cart));
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(Long customerId) {
        Cart cart = cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE)
                .orElseGet(() -> createCart(getCustomer(customerId)));
        return toCartResponse(cart);
    }

    @Transactional
    public CartResponse updateCartItem(Long customerId, Long itemId, CartItemUpdateRequest request) {
        Cart cart = cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Active cart not found"));
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to customer cart");
        }
        item.setQuantity(request.getQuantity());
        cartItemRepository.save(item);
        return toCartResponse(cart);
    }

    @Transactional
    public void removeCartItem(Long customerId, Long itemId) {
        Cart cart = cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Active cart not found"));
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found"));
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Cart item does not belong to customer cart");
        }
        cart.getItems().remove(item);
        cartItemRepository.delete(item);
    }

    @Transactional
    public OrderResponse checkout(CheckoutRequest request) {
        Customer customer = getCustomer(request.getCustomerId());
        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new IllegalArgumentException("Address not found"));
        Cart cart = cartRepository.findByCustomerIdAndStatus(request.getCustomerId(), CartStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("Active cart not found"));
        if (cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty");
        }
        CustomerOrder order = new CustomerOrder();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.CREATED);
        order.setShippingLine1(address.getLine1());
        order.setShippingLine2(address.getLine2());
        order.setShippingCity(address.getCity());
        order.setShippingState(address.getState());
        order.setShippingPostalCode(address.getPostalCode());
        order.setShippingCountry(address.getCountry());

        List<OrderItem> orderItems = cart.getItems().stream().map(item -> {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(item.getProductId());
            orderItem.setProductName(item.getProductName());
            orderItem.setUnitPrice(item.getUnitPrice());
            orderItem.setQuantity(item.getQuantity());
            return orderItem;
        }).collect(Collectors.toList());
        order.setItems(orderItems);
        BigDecimal totalAmount = orderItems.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(totalAmount);
        CustomerOrder savedOrder = orderRepository.save(order);

        Payment payment = new Payment();
        payment.setOrder(savedOrder);
        payment.setAmount(totalAmount);
        payment.setMethod(request.getPaymentMethod());
        payment.setStatus(PaymentStatus.CAPTURED);
        String reference = request.getTransactionReference();
        if (reference == null || reference.isBlank()) {
            reference = UUID.randomUUID().toString();
        }
        payment.setTransactionReference(reference);
        paymentRepository.save(payment);

        savedOrder.setStatus(OrderStatus.PAID);
        orderRepository.save(savedOrder);

        cart.setStatus(CartStatus.CHECKED_OUT);
        cartRepository.save(cart);

        return toOrderResponse(savedOrder, address, payment);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        List<Payment> payments = paymentRepository.findByOrderId(orderId);
        Payment payment = payments.isEmpty() ? null : payments.get(0);
        return toOrderResponse(order, toAddressResponse(order), payment);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersForCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(order -> {
                    List<Payment> payments = paymentRepository.findByOrderId(order.getId());
                    Payment payment = payments.isEmpty() ? null : payments.get(0);
                    return toOrderResponse(order, toAddressResponse(order), payment);
                })
                .collect(Collectors.toList());
    }

    private Customer getCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
    }

    private Cart getOrCreateCart(Long customerId) {
        return cartRepository.findByCustomerIdAndStatus(customerId, CartStatus.ACTIVE)
                .orElseGet(() -> createCart(getCustomer(customerId)));
    }

    private Cart createCart(Customer customer) {
        Cart cart = new Cart();
        cart.setCustomer(customer);
        cart.setStatus(CartStatus.ACTIVE);
        return cartRepository.save(cart);
    }

    private CartResponse toCartResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setCartId(cart.getId());
        response.setCustomerId(cart.getCustomer().getId());
        response.setStatus(cart.getStatus().name());
        List<CartItemResponse> items = cart.getItems().stream()
                .map(this::toCartItemResponse)
                .collect(Collectors.toList());
        response.setItems(items);
        BigDecimal total = items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        response.setTotalAmount(total);
        return response;
    }

    private CartItemResponse toCartItemResponse(CartItem item) {
        CartItemResponse response = new CartItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProductId());
        response.setProductName(item.getProductName());
        response.setUnitPrice(item.getUnitPrice());
        response.setQuantity(item.getQuantity());
        return response;
    }

    private AddressResponse toResponse(Address address) {
        AddressResponse response = new AddressResponse();
        response.setId(address.getId());
        response.setLine1(address.getLine1());
        response.setLine2(address.getLine2());
        response.setCity(address.getCity());
        response.setState(address.getState());
        response.setPostalCode(address.getPostalCode());
        response.setCountry(address.getCountry());
        response.setDefaultAddress(address.isDefaultAddress());
        return response;
    }

    private AddressResponse toAddressResponse(CustomerOrder order) {
        AddressResponse response = new AddressResponse();
        response.setLine1(order.getShippingLine1());
        response.setLine2(order.getShippingLine2());
        response.setCity(order.getShippingCity());
        response.setState(order.getShippingState());
        response.setPostalCode(order.getShippingPostalCode());
        response.setCountry(order.getShippingCountry());
        response.setDefaultAddress(true);
        return response;
    }

    private OrderResponse toOrderResponse(CustomerOrder order, Address address, Payment payment) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerId(order.getCustomer().getId());
        response.setStatus(order.getStatus().name());
        response.setTotalAmount(order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());
        response.setItems(order.getItems().stream()
                .map(this::toOrderItemResponse)
                .collect(Collectors.toList()));
        response.setShippingAddress(toResponse(address));
        response.setPayment(toPaymentResponse(payment));
        return response;
    }

    private OrderResponse toOrderResponse(CustomerOrder order, AddressResponse address, Payment payment) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerId(order.getCustomer().getId());
        response.setStatus(order.getStatus().name());
        response.setTotalAmount(order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());
        response.setItems(order.getItems().stream()
                .map(this::toOrderItemResponse)
                .collect(Collectors.toList()));
        response.setShippingAddress(address);
        response.setPayment(toPaymentResponse(payment));
        return response;
    }

    private OrderItemResponse toOrderItemResponse(OrderItem item) {
        OrderItemResponse response = new OrderItemResponse();
        response.setId(item.getId());
        response.setProductId(item.getProductId());
        response.setProductName(item.getProductName());
        response.setUnitPrice(item.getUnitPrice());
        response.setQuantity(item.getQuantity());
        return response;
    }

    private PaymentResponse toPaymentResponse(Payment payment) {
        if (payment == null) {
            return null;
        }
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setStatus(payment.getStatus().name());
        response.setMethod(payment.getMethod());
        response.setAmount(payment.getAmount());
        response.setTransactionReference(payment.getTransactionReference());
        response.setCreatedAt(payment.getCreatedAt());
        return response;
    }
}
