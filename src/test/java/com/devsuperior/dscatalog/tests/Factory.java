package com.devsuperior.dscatalog.tests;

import com.devsuperior.dscatalog.dtos.ProductDto;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.entities.Product;

import java.time.Instant;

public class Factory {

    public static Product createProduct() {
        Product product = new Product(1L, "Phone", "Good Phone", 800.0, "https://img.com/img.png", Instant.parse("2020-10-20T03:00:00Z"));
        product.getCategories().add(new Category(2L, "Eletronics"));
        return product;
    }

    public static ProductDto createProductDTO(Product product) {
        return new ProductDto(product, product.getCategories());
    }

    public static ProductDto createProductDTO() {
        Product product = createProduct();
        return new ProductDto(product, product.getCategories());
    }

    public static Category createCategory() {
        return new Category(2L, "Eletronics");
    }
}