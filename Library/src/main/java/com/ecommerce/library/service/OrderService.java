package com.ecommerce.library.service;

import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.ShoppingCart;

import java.util.List;

public interface OrderService {
    Order save(ShoppingCart shoppingCart);
    Order acceptOrder(Long id);
    void cancelOrder(Long id);
    List<Order> findAllOrdersByUsername(String username);
    List<Order> findALlOrders();
    Order changePaymentStatus(String responseCode, Long id);
}
