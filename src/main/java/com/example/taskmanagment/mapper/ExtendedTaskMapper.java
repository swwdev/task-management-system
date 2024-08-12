package com.example.taskmanagment.mapper;

import com.example.taskmanagment.dto.task.TaskWithAssignees;
import com.example.taskmanagment.dto.task.TaskWithAuthor;
import com.example.taskmanagment.dto.task.TaskWithComments;
import com.example.taskmanagment.dto.task.TaskWithDetails;
import com.example.taskmanagment.models.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = {
                UserMapper.class, CommentMapper.class
        },
        componentModel = MappingConstants.ComponentModel.SPRING,
        typeConversionPolicy = ReportingPolicy.IGNORE
)
public interface ExtendedTaskMapper {
    TaskWithAssignees toDtoWithAssignees(Task entity);

    TaskWithAuthor toDtoWithAuthor(Task entity);

    TaskWithComments toDtoWithComments(Task entity);

    TaskWithDetails toDtoWithDetails(Task entity);

}
