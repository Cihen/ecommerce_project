package com.ecommerce.customer.controller;

import com.ecommerce.library.model.Customer;
import com.ecommerce.library.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

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
        if(customer.getPhoneNumber().trim().isEmpty() || customer.getAddress().trim().isEmpty() || customer.getCity().trim().isEmpty() || customer.getCountry().trim().isEmpty()) {
            attributes.addFlashAttribute("error", "Hãy điền đầy đủ thông tin cá nhân để có thể checkout !");
            return "redirect:/account";
        }

        return "checkout";
    }
}
