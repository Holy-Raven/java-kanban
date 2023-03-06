package server.pojo;

import tracker.util.enums.Status;
import tracker.util.enums.TaskType;

public class PojoSubtask extends PojoTask {


    private int epicId;

    public PojoSubtask (String name,
                        String description,
                        int id,
                        Status status,
                        TaskType taskType,
                        String startTime,
                        String endTime,
                        int duration,
                        int epicId
    ) {

        super(name, description, id, status, taskType, startTime, endTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

}