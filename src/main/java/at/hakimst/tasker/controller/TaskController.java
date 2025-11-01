package at.hakimst.tasker.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import at.hakimst.tasker.model.Task;
import at.hakimst.tasker.repository.TaskRepository;
import at.hakimst.tasker.service.SummarizationService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    private final TaskRepository repository;
    private final SummarizationService summarizationService;

    public TaskController(TaskRepository repository, SummarizationService summarizationService) {
        this.repository = repository;
        this.summarizationService = summarizationService;
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody TaskRequest req) {
        Task t = new Task();
        t.setTitle(req.title);
        t.setDescription(req.description);
        t.setDueDate(req.dueDate);
        Task saved = repository.save(t);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public ResponseEntity<List<Task>> listTasks(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        List<Task> tasks = repository.findByDueDateBetween(from, to);
        return ResponseEntity.ok(tasks);
    }

    @PostMapping("/summarize")
    public ResponseEntity<SummaryResponse> summarize(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        String summary = summarizationService.summarizeTasksBetween(from, to);
        return ResponseEntity.ok(new SummaryResponse(summary));
    }

    public static class TaskRequest {
        public String title;
        public String description;
        public LocalDate dueDate;
    }

    public static class SummaryResponse {
        public String summary;

        public SummaryResponse(String summary) {
            this.summary = summary;
        }
    }
}
