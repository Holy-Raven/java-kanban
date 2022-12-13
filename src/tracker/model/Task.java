package tracker.model;

import tracker.util.Status;

public class Task {

    private String name;                                        // Название - суть задачи.
    private String description;                                 // Описание раскрывающее детали задачи.
    private int id;                                             // Код - номер задачи.
    private Status status;                                      // Статус выполнения задачи:

    public Task(String name, String description, int id, Status status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
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

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}