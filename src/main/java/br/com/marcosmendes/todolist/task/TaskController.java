package br.com.marcosmendes.todolist.task;

import br.com.marcosmendes.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private ITaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel task, HttpServletRequest request) {
        var userId = request.getAttribute("userId");
        task.setUserId((UUID) userId);

        var currentDate = LocalDateTime.now();
        var isCorrectDate = currentDate.isAfter(task.getStartAt()) || currentDate.isAfter(task.getEndAt());
        if (isCorrectDate) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data de início/fim deve ser maior do que a data atual.");
        }

        if (task.getStartAt().isAfter(task.getEndAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data de inicio deve ser menor que a data de termino");
        }

        var createdTask = this.taskRepository.save(task);
        return ResponseEntity.status(HttpStatus.OK).body(createdTask);
    }

    @GetMapping("/")
    public List<TaskModel> getAll(HttpServletRequest request) {
        var userId = request.getAttribute("userId");
        return this.taskRepository.findTasksByUserId((UUID) userId);
    }

    @PutMapping("/{taskId}")
    public TaskModel update(@RequestBody TaskModel task, @PathVariable UUID taskId, HttpServletRequest request) {
        var selectedTask = this.taskRepository.findById(taskId).orElse(null);
        Utils.copyNonNullProperties(task, selectedTask);

        return this.taskRepository.save(selectedTask);
    }
}
