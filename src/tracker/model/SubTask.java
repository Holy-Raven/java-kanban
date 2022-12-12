package tracker.model;

import tracker.util.Status;

public class SubTask extends Task implements HasId {
    private Epic epic;

    public SubTask(String name, String description, int id, Status status, Epic epic) {
        super(name, description, id, status);
        this.epic = epic;
    }

    public Epic getEpic() {
        return epic;
    }

}