package at.hakimst.tasker.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import at.hakimst.tasker.model.Task;
import at.hakimst.tasker.repository.TaskRepository;
import at.hakimst.tasker.service.SummarizationService;

@Controller
public class WebController {
    private final TaskRepository repository;
    private final SummarizationService summarizationService;

    public WebController(TaskRepository repository, SummarizationService summarizationService) {
        this.repository = repository;
        this.summarizationService = summarizationService;
    }

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(value = "from", required = false) String from,
                        @RequestParam(value = "to", required = false) String to) {
        LocalDate now = LocalDate.now();
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;

        LocalDate f = (from != null) ? LocalDate.parse(from, fmt) : now.minusDays(30);
        LocalDate t = (to != null) ? LocalDate.parse(to, fmt) : now.plusDays(30);

        model.addAttribute("tasks", repository.findByDueDateBetween(f, t));
        model.addAttribute("from", f.format(fmt));
        model.addAttribute("to", t.format(fmt));
        model.addAttribute("summary", "");
        return "index";
    }

    @PostMapping("/create")
    public String createTask(@RequestParam String title,
                             @RequestParam(required = false) String description,
                             @RequestParam String dueDate) {
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        Task t = new Task();
        t.setTitle(title);
        t.setDescription(description);
        t.setDueDate(LocalDate.parse(dueDate, fmt));
        repository.save(t);
        return "redirect:/";
    }

    @PostMapping("/summarize")
    public String summarize(@RequestParam String from, @RequestParam String to, Model model) {
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        LocalDate f = LocalDate.parse(from, fmt);
        LocalDate t = LocalDate.parse(to, fmt);
        String summary = summarizationService.summarizeTasksBetween(f, t);
        model.addAttribute("tasks", repository.findByDueDateBetween(f, t));
        model.addAttribute("from", from);
        model.addAttribute("to", to);
        model.addAttribute("summary", summary);
        return "index";
    }
}
