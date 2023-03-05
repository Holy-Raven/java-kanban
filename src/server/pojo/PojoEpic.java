package server.pojo;

import tracker.util.Status;
import tracker.util.TaskType;

import java.util.ArrayList;
import java.util.List;

public class PojoEpic extends PojoTask{

    public List<Integer> getSubTaskList() {
        return subTaskList;
    }

    public void setSubTaskList(List<Integer> subTaskList) {
        this.subTaskList = subTaskList;
    }

    private List<Integer> subTaskList = new ArrayList<>();

    public PojoEpic(String name,
                    String description,
                    int id,
                    Status status,
                    TaskType taskType,
                    String startTime,
                    String endTime,
                    int duration) {
        super(name, description, id, status, taskType, startTime, endTime, duration);
    }

    public PojoEpic(String name, String description) {
        super(name, description);
        this.subTaskList = subTaskList;
    }
}