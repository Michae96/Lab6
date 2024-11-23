package com.example.taskapp.controllers;

import com.example.taskapp.config.TaskRepository;
import com.example.taskapp.config.CategoryRepository;
import com.example.taskapp.models.Task;
import com.example.taskapp.models.Category;
import com.example.taskapp.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public String listTasks(@RequestParam(defaultValue = "") String search,
                            @RequestParam(defaultValue = "") String status,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "10") int size,
                            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Task> tasks;

        if (status.isEmpty()) {
            tasks = taskRepository.findByTitleContaining(search, pageable);
        } else {
            tasks = taskRepository.findByTitleContainingAndStatus(search, status, pageable);
        }

        model.addAttribute("tasks", tasks);
        model.addAttribute("search", search);
        model.addAttribute("status", status);
        return "tasks";
    }

    @GetMapping("/add")
    public String showAddTaskPage(Model model) {
        model.addAttribute("task", new Task());
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);

        model.addAttribute("statuses", List.of("PENDING", "IN_PROGRESS", "COMPLETED"));
        return "add-task";
    }

    @PostMapping("/add")
    public String addTask(@ModelAttribute Task task, @AuthenticationPrincipal User user) {
        task.setUser(user);

        if (task.getStatus() == null || task.getStatus().isEmpty()) {
            task.setStatus("PENDING");
        }

        taskRepository.save(task);
        return "redirect:/tasks";
    }

    @GetMapping("/edit-task/{id}")
    public String showEditTaskPage(@PathVariable Long id, Model model) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid task ID: " + id));
        model.addAttribute("task", task);

        model.addAttribute("statuses", List.of("PENDING", "IN_PROGRESS", "COMPLETED"));
        return "edit-task";
    }

    @PostMapping("/edit-task/{id}")
    public String updateTask(@PathVariable Long id, @ModelAttribute Task task) {
        task.setId(id);

        if (task.getStatus() == null || task.getStatus().isEmpty()) {
            task.setStatus("PENDING");
        }

        taskRepository.save(task);
        return "redirect:/tasks";
    }

    @GetMapping("/delete-task/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
        return "redirect:/tasks";
    }
}
