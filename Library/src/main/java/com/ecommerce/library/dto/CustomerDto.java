package com.ecommerce.library.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDto {
    @Size(min = 2, max = 12, message = "Firstname should have 2-12 characters !")
    private String firstName;

    @Size(min = 2, max = 12, message = "Lastname should have 2-12 characters !")
    private String lastName;

    private String username;

    @Size(min = 5, max = 20, message = "Password should have 5-20 characters !")
    private String password;

    private String repeatPassword;
}
