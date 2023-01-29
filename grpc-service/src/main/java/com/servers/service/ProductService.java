package com.servers.service;

import com.grpcDemo.Empty;
import com.grpcDemo.Product;
import com.grpcDemo.ProductServiceGrpc;
import com.grpcDemo.User;
import com.servers.mapper.ProductMapper;
import com.servers.repository.ProductRepo;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;


@GrpcService
public class ProductService extends ProductServiceGrpc.ProductServiceImplBase {

    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private ProductMapper productMapper;

    @Override
    public void getProduct(Product request, StreamObserver<Product> responseObserver) {
        productRepo.findAll()
                .stream()
                .filter(product -> product.getId() == request.getId())
                .map(productMapper::toGrpc)
                .findFirst()
                .ifPresent(responseObserver::onNext);
        responseObserver.onCompleted();
    }

    @Override
    public void getAllProducts(User request, StreamObserver<Product> responseObserver) {
        productRepo.findAllByUserId(request.getId())
                .stream()
                .map(productMapper::toGrpc)
                .forEach(responseObserver::onNext);
        responseObserver.onCompleted();
    }

    @Override
    public void getAllProductsInDb(Empty request, StreamObserver<Product> responseObserver) {
        productRepo.findAll()
                .stream()
                .map(productMapper::toGrpc)
                .forEach(responseObserver::onNext);

        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Product> getExpensiveProduct(StreamObserver<Product> responseObserver) {
        return new StreamObserver<Product>() {
            Product expensiveProduct = null;
            double priceTrack = 0;
            @Override
            public void onNext(Product product) {
                if (product.getPrice() > priceTrack){
                    priceTrack = product.getPrice();
                    expensiveProduct = product;
                }
            }

            @Override
            public void onError(Throwable throwable) {
                responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(expensiveProduct);
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public StreamObserver<Product> getProductsBySize(StreamObserver<Product> responseObserver) {
        return new StreamObserver<Product>() {
            List<Product> productList = new ArrayList<>();
            @Override
            public void onNext(Product product) {
                productRepo.findAll()
                        .stream()
                        .map(productMapper::toGrpc)
                        .filter(product1 -> product1.getSize() == product.getSize())
                        .forEach(productList::add);
            }

            @Override
            public void onError(Throwable throwable) {
                    responseObserver.onError(throwable);
            }

            @Override
            public void onCompleted() {
                // to return data after filtering
                productList.forEach(responseObserver::onNext);
                responseObserver.onCompleted();
            }
        };
    }
}
