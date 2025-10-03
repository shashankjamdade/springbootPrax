package com.mongodbprax.dbprax.controller;

import com.mongodbprax.dbprax.model.OrderEntity;
import com.mongodbprax.dbprax.repository.OrderRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Controller
public class OrderGraphQLController {

    @Autowired
    private OrderRepository orderRepository;

    @QueryMapping
    public List<OrderEntity> allOrders() {
        return orderRepository.findAll();
    }

    @QueryMapping
    public OrderEntity orderById(@Argument String orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    @MutationMapping
    public OrderEntity placeOrder(@Argument String userId,
                                  @Argument String itemId,
                                  @Argument int quantity) {
        OrderEntity order = OrderEntity.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .itemId(itemId)
                .quantity(quantity)
                .status("CONFIRMED")
                .createdAt(Instant.now())
                .build();
        return orderRepository.save(order);
    }


}
