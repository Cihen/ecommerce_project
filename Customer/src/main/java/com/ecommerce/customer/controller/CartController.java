package com.ecommerce.customer.controller;

import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.service.CustomerService;
import com.ecommerce.library.service.ProductService;
import com.ecommerce.library.service.ShoppingCartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
public class CartController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private ShoppingCartService cartService;

    @Autowired
    private ProductService productService;

    @GetMapping("/cart")
    public String cart(Model model, Principal principal) {
        if(principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);
        ShoppingCart shoppingCart = customer.getShoppingCart();
        if(shoppingCart == null || shoppingCart.getTotalItems() == 0) {
            model.addAttribute("check", "No item in your cart !");
        }
        model.addAttribute("shoppingCart", shoppingCart);
        model.addAttribute("subTotal", shoppingCart.getTotalPrice());

        return "cart";
    }

    @PostMapping("/add-to-cart")
    public String addItemToCart(@RequestParam("id") Long productId,
                                @RequestParam(value = "quantity", required = true, defaultValue = "1") int quantity,
                                Principal principal,
                                HttpServletRequest request,
                                HttpSession session) {
        if(principal == null) {
            return "redirect:/login";
        }
        Product product = productService.getProductById(productId);
        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);

        ShoppingCart cart = cartService.addItemToCart(product, quantity, customer);

        session.setAttribute("totalItems", cart.getTotalItems());

        return "redirect:" + request.getHeader("Referer");
    }

    @RequestMapping(value = "/update-cart", params = "action=update", method = RequestMethod.POST)
    public String updateCart(@RequestParam("quantity") int quantity,
                             @RequestParam("id") Long productId,
                             Model model,
                             Principal principal,
                             HttpSession session) {
        if(principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);
        Product product = productService.getProductById(productId);
        ShoppingCart cart = cartService.updateItemInCart(product, quantity, customer);

        model.addAttribute("shoppingcart", cart);

        session.setAttribute("totalItems", cart.getTotalItems());

        return "redirect:/cart";
    }

    @RequestMapping(value = "/update-cart", params = "action=delete", method = RequestMethod.POST)
    public String deleteItemFromCart(@RequestParam("id") Long productId,
                                     Model model,
                                     Principal principal,
                                     HttpSession session) {
        if(principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);
        Product product = productService.getProductById(productId);
        ShoppingCart cart = cartService.deleteItemFromCart(product, customer);

        model.addAttribute("shoppingCart", cart);

        session.setAttribute("totalItems", cart.getTotalItems());

        return "redirect:/cart";
    }
}
