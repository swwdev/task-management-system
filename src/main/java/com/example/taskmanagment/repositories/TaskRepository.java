package com.example.taskmanagment.repositories;

import com.example.taskmanagment.models.Priority;
import com.example.taskmanagment.models.Status;
import com.example.taskmanagment.models.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Modifying
    @Query(nativeQuery = true, value = """
            insert into task_assignee (task_id, assignee_id)
            values (:taskId, :id)""")
    void assignUser(Long taskId, Long id);


    @Query("""
            select t
            from Task t
            join t.author u
            where (:userId = u.id) and
            ((:taskHeader is null) or (:taskHeader = t.header)) and
            ((:taskStatus is null) or (:taskStatus = t.status)) and
            ((:taskPriority is null) or (:taskPriority = t.priority))""")
    Page<Task> findAllCreatedTasksBy(Long userId, String taskHeader, Status taskStatus, Priority taskPriority, Pageable pageable);

    @Query("""
            select t
            from Task t
            join t.assignees u
            where :userId = u.id and
            (:header is null or :header = t.header) and
            (:status is null or :status = t.status) and
            (:priority is null or :priority = t.priority)""")
    Page<Task> findAllAssignedTasksBy(Long userId, String header, Status status, Priority priority, Pageable pageable);
}
