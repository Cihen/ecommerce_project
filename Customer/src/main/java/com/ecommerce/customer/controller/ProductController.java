package com.ecommerce.customer.controller;

import com.ecommerce.library.model.Category;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.service.CategoryService;
import com.ecommerce.library.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/products")
    public String products(Model model) {
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
        List<Category> categories = categoryService.findAllByActivated();
        model.addAttribute("categories", categories);
        List<Product> listViewProducts = productService.listViewProducts();
        model.addAttribute("viewProducts", listViewProducts);
        return "shop";
    }

    @GetMapping("/find-product/{id}")
    public String findProductById(@PathVariable("id") Long id, Model model) {
        Product product = productService.getProductById(id);
        model.addAttribute("product", product);
        Long categoryId = product.getCategory().getId();
        List<Product> products = productService.getRelatedProducts(categoryId);
        model.addAttribute("products", products);
        return "product-detail";
    }
}
