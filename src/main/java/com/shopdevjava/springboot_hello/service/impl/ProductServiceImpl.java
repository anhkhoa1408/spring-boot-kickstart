package com.shopdevjava.springboot_hello.service.impl;

import com.shopdevjava.springboot_hello.entities.ProductEntity;
import com.shopdevjava.springboot_hello.repository.ProductRepository;
import com.shopdevjava.springboot_hello.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProductEntity createProduct(ProductEntity product) {
        return productRepository.createProduct(product);
    }

    @Override
    public List<ProductEntity> findAllProducts() {
        return productRepository.findAllProducts();
    }
}
