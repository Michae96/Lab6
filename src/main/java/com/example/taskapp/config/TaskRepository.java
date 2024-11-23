package com.example.taskapp.config;

import com.example.taskapp.models.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findByTitleContainingAndStatus(@Param("title") String title,
                                              @Param("status") String status,
                                              Pageable pageable);

    Page<Task> findByTitleContaining(@Param("title") String title, Pageable pageable);


}