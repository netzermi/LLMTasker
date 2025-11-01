package at.hakimst.tasker.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import at.hakimst.tasker.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByDueDateBetween(LocalDate from, LocalDate to);
}
