package com.shopdevjava.springboot_hello.repository;

import com.shopdevjava.springboot_hello.entities.ProductEntity;

import java.util.List;

public interface ProductRepository {
    ProductEntity createProduct(ProductEntity product);
    List<ProductEntity> findAllProducts();
}
