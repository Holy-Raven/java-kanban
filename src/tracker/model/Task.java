
package tracker.model;

import tracker.service.InMemoryTaskManager;
import tracker.util.Status;
import tracker.util.TaskType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class Task  { //implements Comparable<Task>

    private String name;                                                            // Название - суть задачи.
    private String description;                                                     // Описание раскрывающее детали задачи.
    private int id;                                                                 // Код - номер задачи.
    private Status status;                                                          // Статус выполнения задачи:
    private TaskType taskType;                                                      // Тип задачи
    protected String startTime;                                                     // Начало задачи
    protected String endTime;                                                       // Время завершения задачи
    protected int duration;                                                         // Продолжительность задачи в минутах


    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy|HH:mm"); // определите входной формат времени"

    public Task(String name, String description, int id, Status status, String startTime, int duration) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = getEndTime().format(formatter);
    }

    public Task(String name, String description, Status status) {
        this.name = name;
        this.description = description;
        this.id = InMemoryTaskManager.getId();
        this.status = status;
        this.startTime = "время начала не указано";
        this.duration = 0;
        this.endTime = getEndTimeString();
    }


    public Task(String name, String description, Status status, String startTime, int duration) {
        this.name = name;
        this.description = description;
        this.id = InMemoryTaskManager.getId();
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = getEndTime().format(formatter);
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.id = InMemoryTaskManager.getId();
    }

    public Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public Status getStatus() { return status; }

    public void setStatus(Status status) { this.status = status; }


    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    //обработка полей с датой
    public LocalDateTime getStartTime() {
        if (startTime.equals("время начала не указано")) {
            return null;
        } else {
            return LocalDateTime.parse(startTime, formatter);
        }
    }

    public String getStartTimeString() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime.format(formatter);
    }

    public void setStartTimeString(String startTime) {
        this.startTime = startTime;
    }


    public LocalDateTime getEndTime() {

        if (getStartTime() == null) {
            return null;
        } else {
            return getStartTime().plusMinutes(duration);
        }
    }

    public String getEndTimeString() {
        if (getStartTime() == null) {
            return "нет начала значит нет и конца";
        } else {
            return endTime; //getEndTime().format(formatter);
        }
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime.format(formatter);
    }

    public void setEndTimeString(String endTime) {
        this.endTime = endTime;
    }


    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                ", taskType=" + taskType +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + endTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}