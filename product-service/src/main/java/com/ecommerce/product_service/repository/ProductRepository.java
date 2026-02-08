package com.ecommerce.product_service.repository;

import com.ecommerce.product_service.model.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("""
            select p from Product p
            where lower(p.name) like lower(concat('%', :query, '%'))
               or lower(p.description) like lower(concat('%', :query, '%'))
               or lower(p.brand) like lower(concat('%', :query, '%'))
               or lower(p.category) like lower(concat('%', :query, '%'))
            """)
    List<Product> search(@Param("query") String query);
}
