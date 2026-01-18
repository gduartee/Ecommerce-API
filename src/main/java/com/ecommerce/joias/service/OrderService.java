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
import org.springframework.data.domain.Page;
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
    private final EmailService emailService;

    public OrderService(UserRepository userRepository, OrderRepository orderRepository, AddressRepository addressRepository, ProductVariantRepository productVariantRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.productVariantRepository = productVariantRepository;
        this.emailService = emailService;
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
                new OrderResponseDto.UserOrderDto(
                        savedOrder.getUser().getUserId(),
                        savedOrder.getUser().getName(),
                        savedOrder.getUser().getEmail(),
                        savedOrder.getUser().getPhoneNumber(),
                        savedOrder.getUser().getPhoneNumber()
                ),
                new OrderResponseDto.AddressOrderDto(
                        savedOrder.getAddress().getCep(),
                        savedOrder.getAddress().getStreet(),
                        savedOrder.getAddress().getNum()
                ),
                savedOrder.getOrderItems().stream().map(item -> new OrderResponseDto.OrderItemResponseDto(
                        item.getOrderItemId(),
                        item.getProductVariant().getProduct().getName(),
                        item.getProductVariant().getSize(),
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
                new OrderResponseDto.UserOrderDto(
                        orderEntity.getUser().getUserId(),
                        orderEntity.getUser().getName(),
                        orderEntity.getUser().getEmail(),
                        orderEntity.getUser().getPhoneNumber(),
                        orderEntity.getUser().getPhoneNumber()
                ),
                new OrderResponseDto.AddressOrderDto(
                        orderEntity.getAddress().getCep(),
                        orderEntity.getAddress().getStreet(),
                        orderEntity.getAddress().getNum()
                ),
                orderEntity.getOrderItems().stream().map(orderItem -> new OrderResponseDto.OrderItemResponseDto(
                        orderItem.getOrderItemId(),
                        orderItem.getProductVariant().getProduct().getName(),
                        orderItem.getProductVariant().getSize(),
                        orderItem.getProductVariant().getSku(),
                        orderItem.getQuantity(),
                        orderItem.getUnitPrice(),
                        orderItem.getUnitPrice().multiply(BigDecimal.valueOf(orderItem.getQuantity()))
                )).toList()
        );
    }

    public ApiResponse<OrderResponseDto> listOrders(Integer page, Integer limit, OrderStatus orderStatus) {
        Pageable pageable = PageRequest.of(page, limit);

        Page<Order> pageData;

        if(orderStatus != null)
            pageData = orderRepository.findByStatus(orderStatus, pageable);
        else
            pageData = orderRepository.findAll(pageable);

        var ordersDto = pageData.getContent().stream().map(order -> new OrderResponseDto(
                order.getOrderId(),
                order.getCreatedAt(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getTrackingCode(),
                new OrderResponseDto.UserOrderDto(
                        order.getUser().getUserId(),
                        order.getUser().getName(),
                        order.getUser().getEmail(),
                        order.getUser().getPhoneNumber(),
                        order.getUser().getPhoneNumber()
                ),
                new OrderResponseDto.AddressOrderDto(
                        order.getAddress().getCep(),
                        order.getAddress().getStreet(),
                        order.getAddress().getNum()
                ),
                order.getOrderItems().stream().map(orderItem -> new OrderResponseDto.OrderItemResponseDto(
                        orderItem.getOrderItemId(),
                        orderItem.getProductVariant().getProduct().getName(),
                        orderItem.getProductVariant().getSize(),
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

    public ApiResponse<OrderResponseDto> findAllByUserId(UUID userId, Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page, limit);

        var pageData = orderRepository.findAllByUserUserId(userId, pageable);

        var ordersDto = pageData.getContent().stream().map(order -> new OrderResponseDto(
                order.getOrderId(),
                order.getCreatedAt(),
                order.getTotalPrice(),
                order.getStatus(),
                order.getTrackingCode(),
                new OrderResponseDto.UserOrderDto(
                        order.getUser().getUserId(),
                        order.getUser().getName(),
                        order.getUser().getEmail(),
                        order.getUser().getPhoneNumber(),
                        order.getUser().getPhoneNumber()
                ),
                new OrderResponseDto.AddressOrderDto(
                        order.getAddress().getCep(),
                        order.getAddress().getStreet(),
                        order.getAddress().getNum()
                ),
                order.getOrderItems().stream().map(orderItem -> new OrderResponseDto.OrderItemResponseDto(
                        orderItem.getOrderItemId(),
                        orderItem.getProductVariant().getProduct().getName(),
                        orderItem.getProductVariant().getSize(),
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

    @Transactional
    public void updateTrackingAndShip(Integer orderId, String trackingCode) {
        // 1. Busca o pedido ou estoura erro se não existir
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Pedido não encontrado"));

        // 2. Atualiza os dados e muda o status para ENVIADO (SHIPPED)
        order.setTrackingCode(trackingCode);
        order.setStatus(OrderStatus.SHIPPED);
        orderRepository.save(order);

        // 3. Chama a função de enviar e-mail (Lógica solicitada)
        // Usamos um try-catch aqui para que, se o e-mail falhar, o rastreio continue salvo no banco
        try {
            emailService.sendTrackingEmail(
                    order.getUser().getEmail(),
                    order.getUser().getName(),
                    trackingCode
            );
        } catch (Exception e) {
            System.err.println("Erro ao enviar e-mail de rastreio: " + e.getMessage());
        }
    }

    public Double getTotalFaturado() {
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

    public int getCountPendingDelivery() {
        return orderRepository.countByStatus(OrderStatus.PAID);
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
