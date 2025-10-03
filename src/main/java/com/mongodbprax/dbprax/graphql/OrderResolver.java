//package com.mongodbprax.dbprax.graphql;
//
//import com.mongodbprax.dbprax.model.OrderEntity;
//import com.mongodbprax.dbprax.repository.OrderRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.time.Instant;
//import java.util.List;
//import java.util.UUID;
//
//@Component
//public class OrderResolver implements GraphQLQueryResolver, GraphQLMutationResolver {
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    // Query: Get single order
//    public OrderEntity orderById(String orderId) {
//        return orderRepository.findById(orderId).orElse(null);
//    }
//
//    // Query: Get all orders
//    public List<OrderEntity> allOrders() {
//        return orderRepository.findAll();
//    }
//
//    // Mutation: Place new order
//    public OrderEntity placeOrder(String userId, String itemId, int quantity) {
//        OrderEntity order = OrderEntity.builder()
//                .id(UUID.randomUUID().toString())
//                .userId(userId)
//                .itemId(itemId)
//                .quantity(quantity)
//                .status("CONFIRMED")
//                .createdAt(Instant.now())
//                .build();
//        return orderRepository.save(order);
//    }
//}
