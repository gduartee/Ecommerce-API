package com.ecommerce.joias.repository;

import com.ecommerce.joias.entity.Order;
import com.ecommerce.joias.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.status = :status")
    Double sumTotalPriceByStatus(@Param("status") OrderStatus status);

    Integer countByStatus(OrderStatus orderStatus);

    @Query("SELECT SUM(i.quantity) FROM OrderItem i " +
            "WHERE i.order.status = :status " +
            "AND i.order.createdAt >= :startDate " +
            "AND i.order.createdAt <= :endDate")
    Integer sumQuantityByStatusAndDate(
            @Param("status") OrderStatus status,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    Page<Order> findAllByUserUserId(UUID userId, Pageable pageable);
}
