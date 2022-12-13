package tracker.model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Integer> subTaskList = new ArrayList<>();

    public List<Integer> getSubTaskList() {
        return subTaskList;
    }

    public Epic(String name, String description, int id) {
        super(name, description, id);
    }

}
