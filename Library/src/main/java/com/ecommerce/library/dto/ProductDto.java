package com.ecommerce.library.dto;

import com.ecommerce.library.model.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private String description;
    private Long costPrice;
    private Long salePrice;
    private int currentQuantity;
    private String image;
    private Category category;
    private boolean activated;
    private boolean deleted;
}
