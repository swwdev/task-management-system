package com.example.taskmanagment.mapper;

import com.example.taskmanagment.dto.user.UserCreateDto;
import com.example.taskmanagment.dto.user.UserReadDto;
import com.example.taskmanagment.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = {
                MapperUtil.class
        },
        componentModel = MappingConstants.ComponentModel.SPRING,
        typeConversionPolicy = ReportingPolicy.IGNORE
)
public interface UserMapper {
    @Mapping(target = "password", qualifiedByName = {"MapperUtil", "encode"}, source = "password")
    User toEntity(UserCreateDto dto);

    UserReadDto toDto(User entity);
}
