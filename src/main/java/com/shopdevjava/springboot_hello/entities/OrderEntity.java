package com.shopdevjava.springboot_hello.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "java_order_001")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
