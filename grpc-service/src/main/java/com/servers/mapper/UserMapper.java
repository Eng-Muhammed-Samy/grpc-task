package com.servers.mapper;

import com.grpcDemo.User;
import com.servers.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(User user);

    User toGrpc(UserEntity userEntity);
}
