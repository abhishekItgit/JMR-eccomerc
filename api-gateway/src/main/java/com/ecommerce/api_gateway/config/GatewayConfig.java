package com.ecommerce.api_gateway.config;

import com.ecommerce.api_gateway.filter.JwtAuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public GatewayConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // ✅ Route for Product Service
                .route("product-service", r -> r.path("/api/v1/product-service/**")
                        .filters(f -> f.filter(jwtFilter))   // check JWT before forwarding
                        .uri("lb://PRODUCT-SERVICE"))        // load-balanced by Eureka

                // ✅ Route for User Service
                .route("user-service", r -> r.path("/api/v1/user-service/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://USER-SERVICE"))

                .route("auth-service", r -> r.path("/api/v1/auth/**")
                        .uri("lb://AUTH-SERVICE"))

                .build();
    }
}
