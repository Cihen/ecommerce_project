package com.ecommerce.library.service;

import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.ShoppingCart;

public interface OrderService {
    Order save(ShoppingCart shoppingCart);
    Order acceptOrder(Long id);
    void cancelOrder(Long id);
}
