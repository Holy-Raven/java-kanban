package tracker.model;

import java.util.HashMap;

public class Epic extends Task implements HasId{

    private final HashMap<Integer, SubTask> subTaskList = new HashMap<>();

    public Epic(String name, String description, int id) {
        super(name, description, id);
    }

    public HashMap<Integer, SubTask> getSubTaskList() {
        return subTaskList;
    }
}