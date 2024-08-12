package com.example.taskmanagment.services;

import com.example.taskmanagment.dto.comment.CommentCreateDto;
import com.example.taskmanagment.dto.comment.CommentReadDto;
import com.example.taskmanagment.mapper.CommentMapper;
import com.example.taskmanagment.models.Comment;
import com.example.taskmanagment.repositories.CommentRepository;
import com.example.taskmanagment.validation.validators.TaskValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final TaskValidator taskValidator;

    public CommentReadDto create(CommentCreateDto createDto, String email) {
        taskValidator.validateIsExist(createDto.getTaskId());
        Comment savedComment = commentRepository.save(commentMapper.toEntity(createDto, email));
        return commentMapper.toDto(savedComment);
    }
}
