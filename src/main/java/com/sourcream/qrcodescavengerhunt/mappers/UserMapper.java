package com.sourcream.qrcodescavengerhunt.mappers;

import com.sourcream.qrcodescavengerhunt.domain.dto.UserDto;
import com.sourcream.qrcodescavengerhunt.domain.entities.UserEntity;

@org.mapstruct.Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto mapTo(UserEntity userEntity);

}
