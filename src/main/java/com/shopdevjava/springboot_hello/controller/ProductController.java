package com.shopdevjava.springboot_hello.controller;

import com.shopdevjava.springboot_hello.entities.ProductEntity;
import com.shopdevjava.springboot_hello.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/product")
    private ProductEntity createProduct(ProductEntity product) {
        return productService.createProduct(product);
    }

    @GetMapping("/products")
    private List<ProductEntity> getAllProducts() {
        return productService.findAllProducts();
    }
}
