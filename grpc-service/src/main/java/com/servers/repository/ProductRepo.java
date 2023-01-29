package com.servers.repository;

import com.servers.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<ProductEntity, Integer> {
    List<ProductEntity> findAllByUserId(int userId);;
}
