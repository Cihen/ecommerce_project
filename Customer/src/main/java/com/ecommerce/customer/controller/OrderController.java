package com.ecommerce.customer.controller;

import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.Order;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.service.CustomerService;
import com.ecommerce.library.service.OrderService;
import com.ecommerce.library.service.ShoppingCartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.security.SecureRandom;
import java.util.List;

@Controller
public class OrderController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private ShoppingCartService cartService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/check-out")
    public String checkout(Model model, Principal principal, RedirectAttributes attributes) {
        if(principal == null) {
            return "redirect:/login";
        }
        String username = principal.getName();
        Customer customer = customerService.findByUsername(username);
        ShoppingCart cart = customer.getShoppingCart();
        if(customer.getPhoneNumber() == null || customer.getAddress() == null || customer.getCity() == null || customer.getCountry() == null ||
                customer.getPhoneNumber().isEmpty() || customer.getAddress().isEmpty() || customer.getCity().isEmpty() || customer.getCountry().isEmpty()) {
            attributes.addFlashAttribute("error", "Hãy điền đầy đủ thông tin cá nhân để có thể checkout !");
            return "redirect:/account";
        } else {
            if(cart == null || cart.getTotalItems() == 0) {
                return "redirect:/cart";
            } else {
                model.addAttribute("customer", customer);
                model.addAttribute("cart", cart);
            }
        }
        return "checkout";
    }

    @PostMapping("/add-order")
    public String createOrder(Principal principal,
                              Model model,
                              HttpSession session) {
        if (principal == null) {
            return "redirect:/login";
        } else {
            Customer customer = customerService.findByUsername(principal.getName());
            ShoppingCart cart = customer.getShoppingCart();
            if(cart == null || cart.getTotalItems() == 0) {
                return "redirect:/cart";
            }
            Order order = orderService.save(cart);
//                session.removeAttribute("totalItems");
            model.addAttribute("order", order);
            model.addAttribute("title", "Order Detail");
            model.addAttribute("page", "Order Detail");
            model.addAttribute("success", "Add order successfully");

            session.setAttribute("totalItems", cart.getTotalItems());

            return "redirect:/order";
        }
    }

    @GetMapping("/order")
    public String getOrders(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        } else {
            Customer customer = customerService.findByUsername(principal.getName());
            List<Order> orderList = customer.getOrders();
            model.addAttribute("orders", orderList);
            model.addAttribute("title", "Order");
            model.addAttribute("page", "Order");
            return "order";
        }
    }

    @RequestMapping(value = "/cancel-order", method = {RequestMethod.PUT, RequestMethod.GET})
    public String cancelOrder(Long id, RedirectAttributes attributes) {
        orderService.cancelOrder(id);
        attributes.addFlashAttribute("success", "Cancel order successfully!");
        return "redirect:/orders";
    }
}
