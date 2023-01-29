package com.dev.services;
import com.dev.mapper.ProductMapper;
import com.dev.repository.ProductRepo;
import com.google.protobuf.Descriptors;
import com.grpcDemo.Product;
import com.grpcDemo.ProductServiceGrpc;
import com.grpcDemo.User;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class ProductClientService {

    @Autowired private ProductRepo productRepo;
    @Autowired private ProductMapper productMapper;
    @GrpcClient("grpc-service")
    ProductServiceGrpc.ProductServiceBlockingStub synchronousClient;

    @GrpcClient("grpc-service")
    ProductServiceGrpc.ProductServiceStub asynchronousClient;

    public Map<Descriptors.FieldDescriptor, Object> getProduct(int productId){
        Product product = Product.newBuilder().setId(productId).build();
        Product productResponse = synchronousClient.getProduct(product);
        return productResponse.getAllFields();
    }

    public List<Map<Descriptors.FieldDescriptor, Object>> getAllProductsOfUser(int userId) throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        User user = User.newBuilder().setId(userId).build();
        List<Map<Descriptors.FieldDescriptor, Object>> response = new ArrayList<>();
        asynchronousClient.getAllProducts(user, new StreamObserver<Product>() {
            @Override
            public void onNext(Product product) {
                response.add(product.getAllFields());
            }

            @Override
            public void onError(Throwable throwable) {
                countDownLatch.countDown();
            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }
        });
    boolean count =  countDownLatch.await(1, TimeUnit.MINUTES);
    return count? response : Collections.emptyList();
    }


    public List<Map<Descriptors.FieldDescriptor, Object>> getAllProducts() throws InterruptedException {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        List<Map<Descriptors.FieldDescriptor, Object>> response = new ArrayList<>();
        asynchronousClient.getAllProductsInDb(null, new StreamObserver<Product>() {
            @Override
            public void onNext(Product product) {
                response.add(product.getAllFields());
            }

            @Override
            public void onError(Throwable throwable) {
                countDownLatch.countDown();
            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }
        });

        boolean count = countDownLatch.await(1, TimeUnit.MINUTES);
        return count? response : Collections.emptyList();
    }
    public Map<String, Map<Descriptors.FieldDescriptor, Object>> getExpensiveProduct() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        final Map<String, Map<Descriptors.FieldDescriptor, Object>> response = new HashMap<>();

        StreamObserver<Product> streamObserver = asynchronousClient.getExpensiveProduct(new StreamObserver<Product>() {
            @Override
            public void onNext(Product product) {
                response.put("Expensive Product", product.getAllFields());
            }

            @Override
            public void onError(Throwable throwable) {
                countDownLatch.countDown();
            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }
        });

        //put the list of products to get the expensive one
        productRepo.findAll().stream().map(productMapper::toGrpc).forEach(streamObserver::onNext);
        streamObserver.onCompleted();


        boolean counter = countDownLatch.await(1, TimeUnit.MINUTES);
        return counter? response : Collections.emptyMap();
    }

    public List<Map<Descriptors.FieldDescriptor, Object>> getProductBySize(int size) throws InterruptedException {
        final List<Map<Descriptors.FieldDescriptor, Object>> products = new ArrayList<>();
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        // after server stream back
       StreamObserver streamObserver =  asynchronousClient.getProductsBySize(new StreamObserver<Product>() {
            @Override
            public void onNext(Product product) {
                products.add(product.getAllFields());
            }

            @Override
            public void onError(Throwable throwable) {
                countDownLatch.countDown();
            }

            @Override
            public void onCompleted() {
                countDownLatch.countDown();
            }
        });

       // fetch all data from DB and send it to server
       productRepo.findAll()
               .stream()
               .map(productMapper::toGrpc)
               .filter(product -> product.getSize() == size)
               .forEach(product -> streamObserver.onNext(Product.newBuilder().setSize(product.getSize()).build()));
       streamObserver.onCompleted();


       boolean count = countDownLatch.await(1, TimeUnit.MINUTES);

       return count ? products : Collections.emptyList();
    }
}
