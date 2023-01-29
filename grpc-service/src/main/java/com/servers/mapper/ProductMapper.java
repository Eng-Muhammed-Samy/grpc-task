package com.servers.mapper;

import com.grpcDemo.Product;
import com.servers.entity.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductEntity toEntity(Product product);

    Product toGrpc(ProductEntity product);
}
