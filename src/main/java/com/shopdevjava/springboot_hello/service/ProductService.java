package com.shopdevjava.springboot_hello.service;

import com.shopdevjava.springboot_hello.entities.ProductEntity;

import java.util.List;

public interface ProductService {
    ProductEntity createProduct(ProductEntity product);
    List<ProductEntity> findAllProducts();
}
