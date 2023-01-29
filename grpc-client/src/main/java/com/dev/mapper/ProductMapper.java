package com.dev.mapper;

import com.dev.entity.ProductEntity;
import com.grpcDemo.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductEntity toEntity(Product product);

    Product toGrpc(ProductEntity product);
}
