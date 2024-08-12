package com.example.taskmanagment.mapper;

import com.example.taskmanagment.dto.comment.CommentCreateDto;
import com.example.taskmanagment.dto.comment.CommentReadDto;
import com.example.taskmanagment.models.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        uses = {
                MapperUtil.class, UserMapper.class, TaskMapper.class
        },
        componentModel = MappingConstants.ComponentModel.SPRING,
        typeConversionPolicy = ReportingPolicy.IGNORE
)
public interface CommentMapper {
    @Mapping(target = "author", qualifiedByName = {"MapperUtil", "getAuthor"}, source = "email")
    @Mapping(target = "task", qualifiedByName = {"MapperUtil", "getTask"}, source = "createDto.taskId")
    Comment toEntity(CommentCreateDto createDto, String email);

    CommentReadDto toDto(Comment entity);
}
