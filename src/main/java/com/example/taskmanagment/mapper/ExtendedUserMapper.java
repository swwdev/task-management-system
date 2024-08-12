package com.example.taskmanagment.mapper;

import com.example.taskmanagment.dto.user.UserWithAllTasks;
import com.example.taskmanagment.dto.user.UserWithAssignedTasks;
import com.example.taskmanagment.dto.user.UserWithCreatedTasks;
import com.example.taskmanagment.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = {
                TaskMapper.class
        },
        componentModel = MappingConstants.ComponentModel.SPRING,
        typeConversionPolicy = ReportingPolicy.IGNORE
)
public interface ExtendedUserMapper {
    UserWithAssignedTasks toDtoWithAssignedTasks(User entity);

    UserWithCreatedTasks toDtoWithCreatedTasks(User entity);

    UserWithAllTasks toDtoWitAllTasks(User entity);
}
