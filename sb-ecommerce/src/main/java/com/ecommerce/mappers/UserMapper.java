package com.ecommerce.mappers;

import com.ecommerce.models.User;
import com.ecommerce.security.dto.SignupResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    SignupResponse toSignupResponse(User user);
}
