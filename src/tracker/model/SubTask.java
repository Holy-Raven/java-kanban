
package tracker.model;

import tracker.util.Status;

public class SubTask extends Task {

    private final int epicId;

    public SubTask(String name, String description, Status status, String startTime, int duration, int epicId) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public SubTask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }


    public SubTask(String name, String description, int id, Status status, String startTime, int duration, int epicId) {
        super(name, description, id, status, startTime, duration);
        this.epicId = epicId;
    }


    public int getEpic() {
        return epicId;
    }

    @Override
    public String toString() {
        return "SubTask{" +
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