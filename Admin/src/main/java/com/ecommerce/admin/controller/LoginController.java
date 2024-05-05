package com.ecommerce.admin.controller;

import com.ecommerce.library.dto.AdminDto;
import com.ecommerce.library.model.Admin;
import com.ecommerce.library.service.impl.AdminServiceImpl;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
public class LoginController {
    @Autowired
    private AdminServiceImpl adminService;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String loginForm(Model model, HttpSession session) {
        model.addAttribute("title", "Login");
        session.invalidate();
        return "login";
    }

    @RequestMapping("/index")
    public String home(Model model, Principal principal, HttpSession session) {
        model.addAttribute("title", "Home Page");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        if(principal != null) {
            Admin admin = adminService.findByUsername(principal.getName());
            session.setAttribute("mergeName", admin.mergeName());
        }

        return "index";
    }

    @RequestMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("title", "Dashboard Page");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        return "dashboard";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("title", "Forgot Password");
        return "forgot-password";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("adminDto", new AdminDto());
        model.addAttribute("title", "Register");
        return "register";
    }

    @PostMapping("/register-new")
    public String addNewAdmin(@Valid @ModelAttribute("adminDto") AdminDto adminDto,
                              BindingResult result,
                              Model model) {
        try {
            if(result.hasErrors()) {
                model.addAttribute("adminDto", adminDto);
                result.toString();
                return "register";
            }
            String username = adminDto.getUsername();
            Admin admin = adminService.findByUsername(username);
            if(admin != null) {
                model.addAttribute("adminDto", adminDto);
                System.out.println("Admin not null !");
                model.addAttribute("emailError", "Your email has been registered !");
                return "register";
            }
            if(adminDto.getPassword().equals(adminDto.getRepeatPassword())) {
                adminDto.setPassword(passwordEncoder.encode(adminDto.getPassword()));
                adminService.save(adminDto);
                System.out.println("Success !");
                model.addAttribute("success", "Register successfully !");
                model.addAttribute("adminDto", adminDto);
            } else {
                model.addAttribute("adminDto", adminDto);
                System.out.println("Password not same !");
                model.addAttribute("passwordError", "Your password maybe wrong ! Check again !");
                return "register";
            }
        } catch (Exception e){
            e.printStackTrace();
            model.addAttribute("errors", "Server has been wrong !");
        }
        return "register";
    }
}
