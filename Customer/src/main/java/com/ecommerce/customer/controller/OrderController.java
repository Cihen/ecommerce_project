package com.ecommerce.customer.controller;

import com.ecommerce.library.model.Customer;
import com.ecommerce.library.model.ShoppingCart;
import com.ecommerce.library.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.security.SecureRandom;

@Controller
public class OrderController {
    @Autowired
    private CustomerService customerService;

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

    @GetMapping("/order")
    public String order() {
        return "order";
    }
}
