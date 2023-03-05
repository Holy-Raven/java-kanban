
package tracker.model;

import tracker.util.Status;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    public void setSubTaskList(List<Integer> subTaskList) {
        this.subTaskList = subTaskList;
    }

    private List<Integer> subTaskList = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);

    }

    public Epic(String name, String description, int id) {
        super(name, description, id);
    }

    public Epic(String name, String description, int id, Status status, String startTime, int duration) {
        super(name, description, id, status, startTime, duration);
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
                ", startTime=" + getStartTimeString() +
                ", duration=" + getDuration() +
                ", endTime=" + getEndTimeString() +
                '}';
    }
}
