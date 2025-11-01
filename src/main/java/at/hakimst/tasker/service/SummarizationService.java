package at.hakimst.tasker.service;

import java.time.LocalDate;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.stereotype.Service;

import at.hakimst.tasker.model.Task;
import at.hakimst.tasker.repository.TaskRepository;

@Service
public class SummarizationService {
    private final TaskRepository repository;
    private final GoogleAIClient googleAIClient;

    public SummarizationService(TaskRepository repository, GoogleAIClient googleAIClient) {
        this.repository = repository;
        this.googleAIClient = googleAIClient;
    }

    public String summarizeTasksBetween(LocalDate from, LocalDate to) {
        List<Task> tasks = repository.findByDueDateBetween(from, to);
        if (tasks.isEmpty()) {
            return "No tasks in the specified timeframe.";
        }

        String prompt = buildPrompt(tasks, from, to);
        return googleAIClient.generateText(prompt);
    }

    private String buildPrompt(List<Task> tasks, LocalDate from, LocalDate to) {
        StringJoiner sj = new StringJoiner("\n\n");
        sj.add("You are a helpful assistant. Summarize the following tasks between " + from + " and " + to + ". Provide a concise bullet list and highlight urgent items.");
        for (Task t : tasks) {
            StringBuilder b = new StringBuilder();
            b.append("Title: ").append(t.getTitle()).append("\n");
            b.append("Due: ").append(t.getDueDate()).append("\n");
            if (t.getDescription() != null && !t.getDescription().isEmpty()) {
                b.append("Description: ").append(t.getDescription()).append("\n");
            }
            sj.add(b.toString());
        }
        sj.add("Format: Short bullets, 3-8 bullets, each bullet 1-2 sentences.");
        return sj.toString();
    }
}
