package tracker.model;

import tracker.util.Status;

public class SubTask extends Task {

    private final int epicId;

    public SubTask(String name, String description, int id, Status status, int epicId) {
        super(name, description, id, status);
        this.epicId = epicId;
    }

    public int getEpic() {
        return epicId;
    }

}