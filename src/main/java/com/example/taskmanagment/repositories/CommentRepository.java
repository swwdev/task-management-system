package com.example.taskmanagment.repositories;

import com.example.taskmanagment.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
