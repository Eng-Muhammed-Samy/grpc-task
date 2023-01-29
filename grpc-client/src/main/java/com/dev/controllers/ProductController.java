package com.dev.controllers;

import com.dev.services.ProductClientService;
import com.google.protobuf.Descriptors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ProductController {
    @Autowired
    private ProductClientService productClientService;

    @GetMapping("product/{id}")
    public Map<Descriptors.FieldDescriptor, Object> getProduct(@PathVariable int id){
        return productClientService.getProduct(id);
    }

    @GetMapping("products/{userId}")
    public List<Map<Descriptors.FieldDescriptor, Object>> getAllProductOfUser(@PathVariable int userId){
        try {
            return productClientService.getAllProductsOfUser(userId);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("products")
    public List<Map<Descriptors.FieldDescriptor, Object>> getAllProducts(){
        try {
            return productClientService.getAllProducts();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @GetMapping("expensiveProduct")
    public Map<String,Map<Descriptors.FieldDescriptor, Object>> getExpensiveProduct(){
        try {
            return productClientService.getExpensiveProduct();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("productsSize/{size}")
    public List<Map<Descriptors.FieldDescriptor, Object>> getProductsBySize(@PathVariable int size){
        try {
            return productClientService.getProductBySize(size);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }



}
