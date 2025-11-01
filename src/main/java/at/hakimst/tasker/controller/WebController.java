package at.hakimst.tasker.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import at.hakimst.tasker.model.Task;
import at.hakimst.tasker.repository.TaskRepository;
import at.hakimst.tasker.service.SummarizationService;
import at.hakimst.tasker.util.MarkdownUtils;
import jakarta.validation.Valid;

@Controller
public class WebController {
    private final TaskRepository repository;
    private final SummarizationService summarizationService;
    private final MarkdownUtils markdownUtils;

    public WebController(TaskRepository repository, SummarizationService summarizationService, MarkdownUtils markdownUtils) {
        this.repository = repository;
        this.summarizationService = summarizationService;
        this.markdownUtils = markdownUtils;
    }

    @ModelAttribute("task")
    public Task task() {
        return new Task();
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
        model.addAttribute("renderedSummary", markdownUtils.renderMarkdown(""));
        return "index";
    }

    @PostMapping("/create")
    public String createTask(@Valid @ModelAttribute Task task, BindingResult result, Model model) {
        if (result.hasErrors()) {
            // Re-populate the model with current data for the view
            LocalDate now = LocalDate.now();
            DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
            model.addAttribute("tasks", repository.findByDueDateBetween(now.minusDays(30), now.plusDays(30)));
            model.addAttribute("from", now.minusDays(30).format(fmt));
            model.addAttribute("to", now.plusDays(30).format(fmt));
            model.addAttribute("summary", "");
            model.addAttribute("renderedSummary", markdownUtils.renderMarkdown(""));
            model.addAttribute("errors", result.getAllErrors());
            return "index";
        }
        repository.save(task);
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
        model.addAttribute("renderedSummary", markdownUtils.renderMarkdown(summary));
        return "index";
    }
}
