package com.ecommerce.library.service.impl;

import com.ecommerce.library.model.CartItem;
import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.repository.CartItemRepository;
import com.ecommerce.library.repository.ShoppingCartRepository;
import com.ecommerce.library.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private CartItemRepository itemRepository;

    @Autowired
    private ShoppingCartRepository cartRepository;

    private CartItem findCartItem(Set<CartItem> cartItems, Long productId) {
        if (cartItems == null) {
            return null;
        }
        CartItem cartItem = null;

        for (CartItem item : cartItems) {
            if (item.getProduct().getId() == productId) {
                cartItem = item;
            }
        }
        return cartItem;
    }

    private int totalItems(Set<CartItem> cartItems){
        int totalItems = 0;
        for(CartItem item : cartItems){
            totalItems += item.getQuantity();
        }
        return totalItems;
    }

    private long totalPrice(Set<CartItem> cartItems){
        long totalPrice = 0;

        for(CartItem item : cartItems){
            totalPrice += item.getTotalPrice();
        }
        return totalPrice;
    }

    @Override
    public ShoppingCart addItemToCart(Product product, int quantity, Customer customer) {
        ShoppingCart cart = customer.getShoppingCart();
        if (cart == null) {
            cart = new ShoppingCart();
        }

        Set<CartItem> cartItems = cart.getCartItems();
        if (cartItems == null) {
            cartItems = new HashSet<>();
        }

        CartItem cartItem = findCartItem(cartItems, product.getId());
        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setTotalPrice(quantity * product.getCostPrice());
            cartItem.setQuantity(quantity);
            cartItem.setCart(cart);
            cartItems.add(cartItem);
            itemRepository.save(cartItem);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setTotalPrice(cartItem.getTotalPrice() + ( quantity * product.getCostPrice()));
            itemRepository.save(cartItem);
        }
        cart.setCartItems(cartItems);

        int totalItems = totalItems(cart.getCartItems());
        long totalPrice = totalPrice(cart.getCartItems());

        cart.setTotalPrice(totalPrice);
        cart.setTotalItems(totalItems);
        cart.setCustomer(customer);

        return cartRepository.save(cart);
    }

    @Override
    public ShoppingCart updateItemInCart(Product product, int quantity, Customer customer) {
        ShoppingCart cart = customer.getShoppingCart();

        Set<CartItem> cartItems = cart.getCartItems();

        CartItem item = findCartItem(cartItems, product.getId());
        item.setQuantity(quantity);
        item.setTotalPrice(quantity * product.getCostPrice());
        itemRepository.save(item);

        int totalItems = totalItems(cartItems);
        long totalPrice = totalPrice(cartItems);

        cart.setTotalItems(totalItems);
        cart.setTotalPrice(totalPrice);

        return cartRepository.save(cart);
    }

    @Override
    public ShoppingCart deleteItemFromCart(Product product, Customer customer) {
        ShoppingCart cart = customer.getShoppingCart();

        Set<CartItem> cartItems = cart.getCartItems();
        CartItem item = findCartItem(cartItems, product.getId());
        cartItems.remove(item);
        itemRepository.delete(item);

        long totalPrice = totalPrice(cartItems);
        int totalItems = totalItems(cartItems);

        cart.setCartItems(cartItems);
        cart.setTotalItems(totalItems);
        cart.setTotalPrice(totalPrice);

        return cartRepository.save(cart);
    }

    @Override
    public void emptyCartById(Long id) {
        ShoppingCart shoppingCart = cartRepository.getById(id);
        Set<CartItem> cartItems = new HashSet<>();
        shoppingCart.setCartItems(cartItems);
        itemRepository.deleteAll();
//        for (CartItem item : shoppingCart.getCartItems()) {
//            itemRepository.delete(item);
//        }
//        shoppingCart.setCustomer(null);
        shoppingCart.setTotalPrice(0L);
        shoppingCart.setTotalItems(0);
        cartRepository.save(shoppingCart);
    }
}
