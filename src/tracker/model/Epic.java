package tracker.model;

import tracker.util.Status;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subTaskList = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(String name, String description, int id, Status status) {
        super(name, description, id, status);
    }

    public List<Integer> getSubTaskList() {
        return subTaskList;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", status=" + getStatus() +
                ", taskType=" + getTaskType() +
                '}';
    }
}