package com.example.taskmanagment.mapper;

import com.example.taskmanagment.dto.task.TaskCreatedDto;
import com.example.taskmanagment.dto.task.TaskReadDto;
import com.example.taskmanagment.dto.task.TaskUpdateDto;
import com.example.taskmanagment.models.Task;
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
public interface TaskMapper {

    @Mapping(target = "author", qualifiedByName = {"MapperUtil", "getAuthor"}, source = "email")
    @Mapping(target = "status", expression = "java(MapperUtil.getInitialStatus())")
    @Mapping(target = "priority", qualifiedByName = {"MapperUtil", "getPriority"}, source = "createdDto.priority")
    Task toEntity(TaskCreatedDto createdDto, String email);

    TaskReadDto toDto(Task entity);

    default Task copyNotNullFields(TaskUpdateDto source, Task target) {
        if (source.getHeader() != null)
            target.setHeader(source.getHeader());
        if (source.getDescription() != null)
            target.setDescription(source.getDescription());

        return target;
    }

}
