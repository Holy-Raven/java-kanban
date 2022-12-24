package tracker.model;

import tracker.util.Status;

public class SubTask extends Task {

    private final int epicId;

    public SubTask(String name, String description, Status status, int epicId) {
        super(name, description,status);
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
                '}';
    }
}