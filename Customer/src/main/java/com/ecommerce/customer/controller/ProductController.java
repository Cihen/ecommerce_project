package com.ecommerce.customer.controller;

import com.ecommerce.library.dto.CategoryDto;
import com.ecommerce.library.dto.ProductDto;
import com.ecommerce.library.model.Category;
import com.ecommerce.library.model.Product;
import com.ecommerce.library.service.CategoryService;
import com.ecommerce.library.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/products")
    public String products(Model model) {
        List<CategoryDto> categoryDtoList = categoryService.getCategoriesAndSize();
        model.addAttribute("categories", categoryDtoList);
        List<Product> products = productService.getAllProducts();
        model.addAttribute("products", products);
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

    @GetMapping("/products-in-category/{id}")
    public String getProductsInCategory(@PathVariable("id") Long categoryId, Model model) {
        Category category = categoryService.findById(categoryId);
        List<Product> products = productService.getProductsInCategory(categoryId);
        List<CategoryDto> categoryDtoList = categoryService.getCategoriesAndSize();
        model.addAttribute("categories", categoryDtoList);
        model.addAttribute("category", category);
        model.addAttribute("products", products);
        return "products-in-category";
    }

    @GetMapping("/high-price")
    public String filterHighPrice(Model model) {
        List<CategoryDto> categoryDtoList = categoryService.getCategoriesAndSize();
        model.addAttribute("categories", categoryDtoList);
        List<Product> products = productService.filterHighPrice();
        model.addAttribute("products", products);
        List<Product> listViewProducts = productService.listViewProducts();
        model.addAttribute("viewProducts", listViewProducts);
        return "filter-high-price";
    }

    @GetMapping("/low-price")
    public String filterLowPrice(Model model) {
        List<CategoryDto> categoryDtoList = categoryService.getCategoriesAndSize();
        model.addAttribute("categories", categoryDtoList);
        List<Product> products = productService.filterLowPrice();
        model.addAttribute("products", products);
        List<Product> listViewProducts = productService.listViewProducts();
        model.addAttribute("viewProducts", listViewProducts);
        return "filter-low-price";
    }

    @GetMapping("/search-products")
    public String searchProducts(@RequestParam("keyword") String keyword,
                                 Model model,
                                 Principal principal) {
        if(principal == null) {
            return "redirect:/login";
        }
        List<Product> products = productService.filterSearch(keyword.trim());
        model.addAttribute("title", "Search result");
        model.addAttribute("products", products);

        List<CategoryDto> categoryDtoList = categoryService.getCategoriesAndSize();
        model.addAttribute("categories", categoryDtoList);
        return "result-search";
    }

//    @GetMapping("/search-result/{pageNo}")
//    public String searchProducts(@PathVariable("pageNo") int pageNo,
//                                 @RequestParam("keyword") String keyword,
//                                 Model model,
//                                 Principal principal) {
//        if(principal == null) {
//            return "redirect:/login";
//        }
//        Page<ProductDto> products = productService.searchProducts(pageNo, keyword);
//        model.addAttribute("title", "Search result");
//        model.addAttribute("products", products);
//        model.addAttribute("size", products.getSize());
//        model.addAttribute("totalPages", products.getTotalPages());
//        model.addAttribute("currentPage", pageNo);
//        return "result-products";
//    }
}
