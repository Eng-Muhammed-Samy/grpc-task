package com.dev.mapper;

import com.dev.entity.UserEntity;
import com.grpcDemo.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(User user);

    User toGrpc(UserEntity userEntity);
}
