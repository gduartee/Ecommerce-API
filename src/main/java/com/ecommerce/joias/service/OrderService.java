package com.ecommerce.joias.service;

import com.ecommerce.joias.dto.create.CreateOrderDto;
import com.ecommerce.joias.dto.response.ApiResponse;
import com.ecommerce.joias.dto.response.OrderResponseDto;
import com.ecommerce.joias.dto.update.UpdateOrderDto;
import com.ecommerce.joias.entity.Order;
import com.ecommerce.joias.entity.OrderItem;
import com.ecommerce.joias.entity.enums.OrderStatus;
import com.ecommerce.joias.repository.AddressRepository;
import com.ecommerce.joias.repository.OrderRepository;
import com.ecommerce.joias.repository.ProductVariantRepository;
import com.ecommerce.joias.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.UUID;

@Service
public class OrderService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final ProductVariantRepository productVariantRepository;

    public OrderService(UserRepository userRepository, OrderRepository orderRepository, AddressRepository addressRepository, ProductVariantRepository productVariantRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.productVariantRepository = productVariantRepository;
    }

    @Transactional
    public OrderResponseDto createOrder(UUID userId, Integer addressId, CreateOrderDto createOrderDto) {

        var user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        var address = addressRepository.findById(addressId).orElseThrow(() -> new RuntimeException("Endereço não encontrado"));

        if (!address.getUser().getUserId().equals(userId))
            throw new RuntimeException("Endereço não pertence a este usuário");

        var order = new Order();
        order.setUser(user);
        order.setAddress(address);
        order.setStatus(OrderStatus.WAITING_PAYMENT);
        order.setCreatedAt(LocalDateTime.now());
        order.setOrderItems(new ArrayList<>());

        BigDecimal total = BigDecimal.ZERO;

        // Processa itens e estoque
        for (var itemDto : createOrderDto.items()) {
            var productVariant = productVariantRepository.findById(itemDto.variantId()).orElseThrow(() -> new RuntimeException("Variante não encontrada"));

            // Valida o estoque
            if (productVariant.getStockQuantity() < itemDto.quantity())
                throw new RuntimeException("Estoque insuficiente para o item " + productVariant.getSku());

            // Baixa estoque
            productVariant.setStockQuantity(productVariant.getStockQuantity() - itemDto.quantity());
            productVariantRepository.save(productVariant);

            // Cria item
            var orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductVariant(productVariant);
            orderItem.setQuantity(itemDto.quantity());
            orderItem.setUnitPrice(productVariant.getPrice()); // Preço histórico

            order.addOrderItem(orderItem);

            // Soma total
            total = total.add(productVariant.getPrice().multiply(BigDecimal.valueOf(itemDto.quantity())));
        }

        order.setTotalPrice(total);

        var savedOrder = orderRepository.save(order);

        return new OrderResponseDto(
                savedOrder.getOrderId(),
                savedOrder.getCreatedAt(),
                savedOrder.getTotalPrice(),
                savedOrder.getStatus(),
                savedOrder.getTrackingCode(),
                savedOrder.getOrderItems().stream().map(item -> new OrderResponseDto.OrderItemResponseDto(
                        item.getOrderItemId(),
                        item.getProductVariant().getSku(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
                )).toList()
        );
    }

    public OrderResponseDto getOrderById(Integer orderId) {
        var orderEntity = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        return new OrderResponseDto(
                orderEntity.getOrderId(),
                orderEntity.getCreatedAt(),
                orderEntity.getTotalPrice(),
                orderEntity.getStatus(),
                orderEntity.getTrackingCode(),
                orderEntity.getOrderItems().stream().map(orderItem -> new OrderResponseDto.OrderItemResponseDto(
                        orderItem.getOrderItemId(),
                        orderItem.getProductVariant().getSku(),
                        orderItem.getQuantity(),
                        orderItem.getUnitPrice(),
                        orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()))
                )).toList()
        );
    }

    public ApiResponse<OrderResponseDto> listOrders(Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page, limit);

        var pageData = orderRepository.findAll(pageable);

        var ordersDto = pageData.getContent().stream().map(order -> new OrderResponseDto(
                order.getOrderId(),
                order.getCreatedAt(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getTrackingCode(),
                order.getOrderItems().stream().map(orderItem -> new OrderResponseDto.OrderItemResponseDto(
                        orderItem.getOrderItemId(),
                        orderItem.getProductVariant().getSku(),
                        orderItem.getQuantity(),
                        orderItem.getUnitPrice(),
                        orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()))
                )).toList()
        )).toList();

        return new ApiResponse<>(
                ordersDto,
                pageData.getTotalElements(),
                pageData.getTotalPages(),
                pageData.getNumber(),
                pageData.getSize()
        );
    }

    public void updateOrderById(Integer orderId, UpdateOrderDto updateOrderDto) {
        var order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        if (updateOrderDto.trackingCode() != null && !updateOrderDto.trackingCode().isBlank())
            order.setTrackingCode(updateOrderDto.trackingCode());

        if (updateOrderDto.status() == OrderStatus.CANCELED && order.getStatus() != OrderStatus.CANCELED)
            devolverEstoque(order);

        if (updateOrderDto.status() != null)
            order.setStatus(updateOrderDto.status());

        orderRepository.save(order);
    }

    public Double getTotalFaturado(){
        Double total = orderRepository.sumTotalPriceByStatus(OrderStatus.PAID);
        return total != null ? total : 0.0;
    }

    public Integer getProdutosVendidosNoMes() {
        LocalDateTime now = LocalDateTime.now();
        // Primeiro dia do mês às 00:00:00
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        // Último dia do mês às 23:59:59
        LocalDateTime endOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);

        Integer total = orderRepository.sumQuantityByStatusAndDate(
                OrderStatus.PAID,
                startOfMonth,
                endOfMonth
        );

        return total != null ? total : 0;
    }

    public int getCountPendingDelivery(){
        return  orderRepository.countByStatus(OrderStatus.PAID);
    }

    public void deleteOrderById(Integer orderId) {
        orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        orderRepository.deleteById(orderId);
    }

    private void devolverEstoque(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            var productVariant = item.getProductVariant();
            productVariant.setStockQuantity(productVariant.getStockQuantity() + item.getQuantity());
        }
    }


}
